package team.themoment.readygsmserver.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import team.themoment.readygsmserver.domain.chat.client.ChatCompletionClient;
import team.themoment.readygsmserver.domain.chat.client.StreamHandler;
import team.themoment.readygsmserver.domain.chat.client.StreamSubscription;
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
 * <p>AI 응답은 도착하는 즉시 재전송한다. 어디에서도 모았다가 한 번에 보내지 않는다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamChatAnswerService {

    private static final long EMITTER_TIMEOUT_MILLIS = 180_000L;
    private static final long HEARTBEAT_INTERVAL_SECONDS = 15L;
    private static final String ERROR_REASON_UPSTREAM_INTERRUPTED = "upstream_interrupted";

    /** 프롬프트 조립은 다음 단계에서 붙인다. */
    private static final String PLACEHOLDER_SYSTEM_PROMPT = "";

    private final ConversationRepository conversationRepository;
    private final ChatCompletionClient chatCompletionClient;

    @Qualifier("chatHeartbeatScheduler")
    private final ScheduledExecutorService chatHeartbeatScheduler;

    public SseEmitter execute(String sessionId, ChatAskReqDto req) {
        if (!conversationRepository.exists(sessionId)) {
            throw new ExpectedException("만료되었거나 존재하지 않는 채팅 세션입니다.", HttpStatus.NOT_FOUND);
        }

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

        stream.start(List.of(ChatMessage.user(req.message())));

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

        private ChatStream(String sessionId, SseEmitter emitter) {
            this.sessionId = sessionId;
            this.emitter = emitter;
        }

        private void start(List<ChatMessage> messages) {
            heartbeat.set(chatHeartbeatScheduler.scheduleAtFixedRate(
                    this::sendHeartbeat,
                    HEARTBEAT_INTERVAL_SECONDS,
                    HEARTBEAT_INTERVAL_SECONDS,
                    TimeUnit.SECONDS
            ));
            StreamSubscription started = chatCompletionClient.stream(PLACEHOLDER_SYSTEM_PROMPT, messages, this);
            if (finished.get()) {
                started.cancel();
            } else {
                subscription.set(started);
            }
        }

        @Override
        public void onToken(String token) {
            send(SseEmitter.event().data(token));
        }

        @Override
        public void onComplete(String finishReason) {
            if (!finished.compareAndSet(false, true)) {
                return;
            }
            send(SseEmitter.event().name("done").data(new ChatDoneResDto(finishReason)));
            stopHeartbeat();
            emitter.complete();
        }

        @Override
        public void onError(Throwable e) {
            if (!finished.compareAndSet(false, true)) {
                return;
            }
            log.warn("[CHAT] 응답 생성 실패 sessionId={}", sessionId, e);
            send(SseEmitter.event().name("error").data(new ChatErrorResDto(ERROR_REASON_UPSTREAM_INTERRUPTED)));
            stopHeartbeat();
            emitter.complete();
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
