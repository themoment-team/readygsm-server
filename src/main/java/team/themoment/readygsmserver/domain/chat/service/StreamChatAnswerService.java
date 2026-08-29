package team.themoment.readygsmserver.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import team.themoment.readygsmserver.domain.chat.dto.request.ChatAskReqDto;
import team.themoment.readygsmserver.domain.chat.dto.response.ChatDoneResDto;
import team.themoment.readygsmserver.domain.chat.repository.ConversationRepository;
import team.themoment.sdk.exception.ExpectedException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 질문을 받아 답변을 SSE로 흘려보낸다.
 *
 * <p>아직 AI를 붙이지 않은 단계라 고정 문장을 토큰 단위로 내보낸다.
 * 응답 계약(토큰 {@code data:} → {@code event: done})은 최종 형태와 동일하므로
 * 이 시점부터 프론트가 붙을 수 있다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamChatAnswerService {

    private static final long EMITTER_TIMEOUT_MILLIS = 180_000L;
    private static final long TOKEN_INTERVAL_MILLIS = 40L;
    private static final int TOKEN_LENGTH = 3;
    private static final String FINISH_REASON_STOP = "stop";

    private static final String PLACEHOLDER_ANSWER =
            "원서 접수 시에는 지원서와 자기소개서를 준비해 주세요. "
                    + "제출 서류는 접수 기간 내에 온라인으로 업로드하시면 됩니다.";

    private final ConversationRepository conversationRepository;

    @Qualifier("chatStreamExecutor")
    private final Executor chatStreamExecutor;

    public SseEmitter execute(String sessionId, ChatAskReqDto req) {
        validateSession(sessionId);

        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MILLIS);
        AtomicBoolean cancelled = new AtomicBoolean(false);

        emitter.onCompletion(() -> cancelled.set(true));
        emitter.onTimeout(() -> {
            cancelled.set(true);
            log.info("[CHAT] 응답 스트림 타임아웃 sessionId={}", sessionId);
        });
        emitter.onError(e -> {
            cancelled.set(true);
            log.info("[CHAT] 응답 스트림 오류 sessionId={}", sessionId, e);
        });

        chatStreamExecutor.execute(() -> stream(emitter, cancelled, sessionId));

        return emitter;
    }

    private void validateSession(String sessionId) {
        if (!conversationRepository.exists(sessionId)) {
            throw new ExpectedException("만료되었거나 존재하지 않는 채팅 세션입니다.", HttpStatus.NOT_FOUND);
        }
    }

    private void stream(SseEmitter emitter, AtomicBoolean cancelled, String sessionId) {
        try {
            for (int index = 0; index < PLACEHOLDER_ANSWER.length(); index += TOKEN_LENGTH) {
                if (cancelled.get()) {
                    log.info("[CHAT] 클라이언트 연결이 끊겨 응답 스트림을 취소합니다 sessionId={}", sessionId);
                    return;
                }
                int end = Math.min(index + TOKEN_LENGTH, PLACEHOLDER_ANSWER.length());
                emitter.send(SseEmitter.event().data(PLACEHOLDER_ANSWER.substring(index, end)));
                Thread.sleep(TOKEN_INTERVAL_MILLIS);
            }
            emitter.send(SseEmitter.event()
                    .name("done")
                    .data(new ChatDoneResDto(FINISH_REASON_STOP)));
            emitter.complete();
        } catch (IOException e) {
            log.info("[CHAT] 응답 전송 실패, 클라이언트 연결이 끊긴 것으로 보입니다 sessionId={}", sessionId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            emitter.completeWithError(e);
        }
    }
}
