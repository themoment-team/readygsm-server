package team.themoment.readygsmserver.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import team.themoment.readygsmserver.domain.chat.dto.request.ChatAskReqDto;
import team.themoment.readygsmserver.domain.chat.dto.response.ChatDoneResDto;
import team.themoment.readygsmserver.domain.chat.dto.response.ChatErrorResDto;
import team.themoment.readygsmserver.domain.chat.entity.ChatMessage;
import team.themoment.readygsmserver.domain.chat.entity.Conversation;
import team.themoment.readygsmserver.domain.chat.repository.ConversationRepository;
import team.themoment.sdk.exception.ExpectedException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 질문을 받아 답변을 SSE로 흘려보낸다.
 *
 * <p>처리 순서는 다음과 같다.
 * <ol>
 *   <li>Redis에서 이전 대화를 조회하고 요청한 사용자의 것인지 확인한다</li>
 *   <li>시스템 프롬프트와 메시지 배열을 조립한다</li>
 *   <li>이번 질문을 Redis에 저장한다</li>
 *   <li>토큰을 받는 즉시 전송하면서 동시에 버퍼에 누적한다</li>
 *   <li>정상 완료면 버퍼를 저장하고, 중단되면 버퍼를 버린다</li>
 * </ol>
 *
 * <p>버퍼는 저장용일 뿐이다. 전송은 언제나 즉시이며 어디에서도 모았다가 한 번에 보내지 않는다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamChatAnswerService {

    /** 토큰이 계속 오는 긴 답변의 절대 상한. 유휴 타임아웃과는 성격이 다르다. */
    private static final long EMITTER_TIMEOUT_MILLIS = 180_000L;

    private static final long HEARTBEAT_INTERVAL_SECONDS = 15L;

    /**
     * 마지막 토큰 이후 이만큼 조용하면 끊는다. upstream이 오류 없이 멈추는 경우가 있는데,
     * 이게 없으면 emitter 타임아웃 180초까지 멈춘 화면을 그대로 보게 된다.
     *
     * <p>첫 토큰을 기다리는 구간에도 적용된다. TTFT가 길어야 15초 안팎이라 오탐 여지는 없다.
     */
    private static final long IDLE_TIMEOUT_MILLIS = 30_000L;

    private static final String ERROR_REASON_UPSTREAM_INTERRUPTED = "upstream_interrupted";
    private static final String ERROR_REASON_IDLE_TIMEOUT = "idle_timeout";

    private final ConversationRepository conversationRepository;
    private final ChatCompletionClient chatCompletionClient;
    private final ChatPromptAssembler chatPromptAssembler;

    @Qualifier("chatHeartbeatScheduler")
    private final ScheduledExecutorService chatHeartbeatScheduler;

    public SseEmitter execute(Long userId, String sessionId, ChatAskReqDto req) {
        // 남의 세션을 넘겨도 만료와 똑같이 404다. 존재 여부를 알려주면 sessionId를 넣어보며
        // 유효한 값을 찾아낼 수 있다
        Conversation conversation = conversationRepository.find(sessionId)
                .filter(it -> it.ownedBy(userId))
                .orElseThrow(() -> new ExpectedException(
                        "만료되었거나 존재하지 않는 채팅 세션입니다.", HttpStatus.NOT_FOUND));

        List<ChatMessage> history = conversation.messages();
        String systemPrompt = chatPromptAssembler.assembleSystemPrompt(req.message());
        List<ChatMessage> messages = chatPromptAssembler.assembleMessages(history, req.message());
        chatPromptAssembler.logAssembled(systemPrompt, messages);

        conversationRepository.append(sessionId, ChatMessage.user(req.message()));

        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MILLIS);
        ChatStream stream = new ChatStream(sessionId, req.message(), emitter);

        emitter.onCompletion(stream::release);
        emitter.onTimeout(() -> {
            log.info("[CHAT] 응답 스트림 타임아웃 sessionId={}", sessionId);
            stream.release();
        });
        emitter.onError(e -> {
            log.info("[CHAT] 응답 스트림 오류 sessionId={}", sessionId);
            stream.release();
        });

        stream.start(systemPrompt, messages);

        return emitter;
    }

    /**
     * emitter 하나에 대응하는 스트림 상태.
     *
     * <p>토큰 전송은 스트림 스레드에서, heartbeat는 스케줄러 스레드에서 일어나므로
     * 모든 쓰기를 하나의 락으로 직렬화한다.
     */
    private final class ChatStream implements StreamHandler {

        private final String sessionId;
        private final String question;
        private final SseEmitter emitter;
        private final Object sendLock = new Object();
        private final AtomicBoolean finished = new AtomicBoolean(false);
        private final AtomicReference<StreamSubscription> subscription = new AtomicReference<>();
        private final AtomicReference<ScheduledFuture<?>> heartbeat = new AtomicReference<>();

        /** 저장용 누적 버퍼. 전송과는 무관하다. */
        private final StringBuilder answer = new StringBuilder();

        /** 유휴 판정 기준. 스트림 스레드가 쓰고 스케줄러 스레드가 읽는다. */
        private volatile long lastTokenAt;

        private ChatStream(String sessionId, String question, SseEmitter emitter) {
            this.sessionId = sessionId;
            this.question = question;
            this.emitter = emitter;
        }

        private void start(String systemPrompt, List<ChatMessage> messages) {
            lastTokenAt = System.currentTimeMillis();
            heartbeat.set(chatHeartbeatScheduler.scheduleAtFixedRate(
                    this::tick,
                    HEARTBEAT_INTERVAL_SECONDS,
                    HEARTBEAT_INTERVAL_SECONDS,
                    TimeUnit.SECONDS
            ));
            StreamSubscription started = chatCompletionClient.stream(systemPrompt, messages, this);
            if (finished.get()) {
                started.cancel();
            } else {
                subscription.set(started);
            }
        }

        @Override
        public void onToken(String token) {
            lastTokenAt = System.currentTimeMillis();
            answer.append(token);
            send(SseEmitter.event().data(token));
        }

        @Override
        public void onComplete(String finishReason) {
            if (!finished.compareAndSet(false, true)) {
                return;
            }
            saveAnswer();
            send(SseEmitter.event().name("done").data(new ChatDoneResDto(finishReason)));
            finish();
        }

        @Override
        public void onError(Throwable e) {
            log.warn("[CHAT] 응답 생성 실패 sessionId={}", sessionId, e);
            fail(ERROR_REASON_UPSTREAM_INTERRUPTED);
        }

        @Override
        public void onAbort() {
            if (!finished.compareAndSet(false, true)) {
                return;
            }
            log.info("[CHAT] 신호 없이 스트림을 끊습니다 sessionId={}", sessionId);
            finish();
        }

        /** 중단된 답변은 저장하지 않는다. 다음 질문의 맥락을 오염시킨다. */
        private void fail(String reason) {
            if (!finished.compareAndSet(false, true)) {
                return;
            }
            send(SseEmitter.event().name("error").data(new ChatErrorResDto(reason)));
            finish();
        }

        /** heartbeat를 멈추고 upstream을 놓아준 뒤 응답을 닫는다. */
        private void finish() {
            stopHeartbeat();
            cancelSubscription();
            emitter.complete();
        }

        /** 정상 완료된 답변만 저장한다. 중단된 답변은 버퍼째 버린다. */
        private void saveAnswer() {
            if (answer.isEmpty()) {
                return;
            }
            String content = answer.toString();
            logIfUnanswerable(content);
            conversationRepository.append(sessionId, ChatMessage.assistant(content));
        }

        /**
         * 답변하지 못한 질문을 남긴다. FAQ에 무엇을 추가해야 할지 알려주는 지표다.
         * 1차 버전에서 저장할 가치가 있는 유일한 데이터다.
         */
        private void logIfUnanswerable(String content) {
            if (content.contains(ChatPromptAssembler.UNANSWERABLE_MARKER)) {
                log.warn("[CHAT] FAQ로 답변하지 못한 질문 question={}", question);
            }
        }

        /**
         * heartbeat 전송과 유휴 검사를 겸한다. 검사 주기가 15초라 유휴 판정은
         * 30초 정확히가 아니라 30~45초 사이에 일어난다.
         */
        private void tick() {
            if (System.currentTimeMillis() - lastTokenAt >= IDLE_TIMEOUT_MILLIS) {
                log.warn("[CHAT] upstream이 {}초 넘게 조용해 스트림을 정리합니다 sessionId={}",
                        IDLE_TIMEOUT_MILLIS / 1000, sessionId);
                fail(ERROR_REASON_IDLE_TIMEOUT);
                return;
            }
            send(SseEmitter.event().comment("ping"));
        }

        private void send(SseEmitter.SseEventBuilder event) {
            synchronized (sendLock) {
                try {
                    emitter.send(event);
                } catch (IOException | IllegalStateException e) {
                    log.debug("[CHAT] 전송 실패, 클라이언트 연결이 끊긴 것으로 보입니다 sessionId={}", sessionId);
                    release();
                }
            }
        }

        /** 연결이 끝났으므로 heartbeat를 멈추고 upstream 구독을 해제한다. */
        private void release() {
            if (finished.compareAndSet(false, true)) {
                log.info("[CHAT] 연결이 종료되어 upstream 요청을 취소합니다 sessionId={}", sessionId);
            }
            stopHeartbeat();
            cancelSubscription();
        }

        /**
         * 이미 끝난 구독에 cancel을 부르는 것은 안전하다. 반대로 부르지 않으면
         * 멈춘 upstream 요청이 자기 타임아웃까지 남는다.
         */
        private void cancelSubscription() {
            StreamSubscription current = subscription.getAndSet(null);
            if (current != null) {
                current.cancel();
            }
        }

        private void stopHeartbeat() {
            ScheduledFuture<?> current = heartbeat.getAndSet(null);
            if (current != null) {
                current.cancel(false);
            }
        }
    }
}
