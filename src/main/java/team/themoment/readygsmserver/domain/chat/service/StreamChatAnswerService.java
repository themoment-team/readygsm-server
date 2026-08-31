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
 *   <li>Redis에서 이전 대화를 조회한다</li>
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

    private static final long EMITTER_TIMEOUT_MILLIS = 180_000L;
    private static final long HEARTBEAT_INTERVAL_SECONDS = 15L;
    private static final String ERROR_REASON_UPSTREAM_INTERRUPTED = "upstream_interrupted";

    private final ConversationRepository conversationRepository;
    private final ChatCompletionClient chatCompletionClient;
    private final ChatPromptAssembler chatPromptAssembler;

    @Qualifier("chatHeartbeatScheduler")
    private final ScheduledExecutorService chatHeartbeatScheduler;

    public SseEmitter execute(String sessionId, ChatAskReqDto req) {
        if (!conversationRepository.exists(sessionId)) {
            throw new ExpectedException("만료되었거나 존재하지 않는 채팅 세션입니다.", HttpStatus.NOT_FOUND);
        }

        List<ChatMessage> history = conversationRepository.findMessages(sessionId);
        String systemPrompt = chatPromptAssembler.assembleSystemPrompt(req.message());
        List<ChatMessage> messages = chatPromptAssembler.assembleMessages(history, req.message());
        chatPromptAssembler.logAssembled(systemPrompt, messages);

        conversationRepository.append(sessionId, ChatMessage.user(req.message()));

        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MILLIS);
        ChatStream stream = new ChatStream(sessionId, emitter);

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
        private final SseEmitter emitter;
        private final Object sendLock = new Object();
        private final AtomicBoolean finished = new AtomicBoolean(false);
        private final AtomicReference<StreamSubscription> subscription = new AtomicReference<>();
        private final AtomicReference<ScheduledFuture<?>> heartbeat = new AtomicReference<>();

        /** 저장용 누적 버퍼. 전송과는 무관하다. */
        private final StringBuilder answer = new StringBuilder();

        private ChatStream(String sessionId, SseEmitter emitter) {
            this.sessionId = sessionId;
            this.emitter = emitter;
        }

        private void start(String systemPrompt, List<ChatMessage> messages) {
            heartbeat.set(chatHeartbeatScheduler.scheduleAtFixedRate(
                    this::sendHeartbeat,
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
            stopHeartbeat();
            emitter.complete();
        }

        @Override
        public void onError(Throwable e) {
            if (!finished.compareAndSet(false, true)) {
                return;
            }
            log.warn("[CHAT] 응답 생성 실패, 답변을 저장하지 않습니다 sessionId={}", sessionId, e);
            send(SseEmitter.event().name("error").data(new ChatErrorResDto(ERROR_REASON_UPSTREAM_INTERRUPTED)));
            stopHeartbeat();
            emitter.complete();
        }

        /** 정상 완료된 답변만 저장한다. 중단된 답변은 버퍼째 버린다. */
        private void saveAnswer() {
            if (answer.isEmpty()) {
                return;
            }
            conversationRepository.append(sessionId, ChatMessage.assistant(answer.toString()));
        }

        private void sendHeartbeat() {
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
            boolean wasRunning = finished.compareAndSet(false, true);
            stopHeartbeat();
            StreamSubscription current = subscription.getAndSet(null);
            if (current != null && wasRunning) {
                log.info("[CHAT] 연결이 종료되어 upstream 요청을 취소합니다 sessionId={}", sessionId);
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
