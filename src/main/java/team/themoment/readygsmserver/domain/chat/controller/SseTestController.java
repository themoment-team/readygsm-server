package team.themoment.readygsmserver.domain.chat.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSE 파이프라인이 응답을 모으지 않고 즉시 흘려보내는지 확인하기 위한 검증용 엔드포인트다.
 * 비즈니스 로직은 없다.
 *
 * <p>검증: {@code curl -N http://localhost:8080/api/v1/chat/test-sse}
 * ({@code -N} 없이 호출하면 curl이 버퍼링해서 확인할 수 없다)
 */
@Slf4j
@Hidden
@RestController
@RequiredArgsConstructor
public class SseTestController {

    private static final long EMITTER_TIMEOUT_MILLIS = 180_000L;
    private static final long SEND_INTERVAL_MILLIS = 1_000L;
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Qualifier("chatStreamExecutor")
    private final Executor chatStreamExecutor;

    @GetMapping(value = "/api/v1/chat/test-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> testSse() {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MILLIS);
        AtomicBoolean cancelled = new AtomicBoolean(false);

        emitter.onCompletion(() -> {
            cancelled.set(true);
            log.debug("[CHAT] SSE 테스트 스트림 종료");
        });
        emitter.onTimeout(() -> {
            cancelled.set(true);
            log.info("[CHAT] SSE 테스트 스트림 타임아웃");
        });
        emitter.onError(e -> {
            cancelled.set(true);
            log.info("[CHAT] SSE 테스트 스트림 오류", e);
        });

        chatStreamExecutor.execute(() -> sendAlphabet(emitter, cancelled));

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no")
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(emitter);
    }

    private void sendAlphabet(SseEmitter emitter, AtomicBoolean cancelled) {
        try {
            for (char letter : ALPHABET.toCharArray()) {
                if (cancelled.get()) {
                    log.info("[CHAT] 클라이언트 연결이 끊겨 SSE 테스트 스트림을 취소합니다");
                    return;
                }
                emitter.send(SseEmitter.event().data(String.valueOf(letter)));
                Thread.sleep(SEND_INTERVAL_MILLIS);
            }
            emitter.complete();
        } catch (IOException e) {
            log.info("[CHAT] SSE 테스트 스트림 전송 실패, 클라이언트 연결이 끊긴 것으로 보입니다");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            emitter.completeWithError(e);
        }
    }
}
