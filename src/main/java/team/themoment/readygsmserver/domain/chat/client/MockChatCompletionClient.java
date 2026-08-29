package team.themoment.readygsmserver.domain.chat.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import team.themoment.readygsmserver.domain.chat.entity.ChatMessage;
import team.themoment.readygsmserver.domain.chat.entity.constant.ChatRole;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * OpenAI를 대신해 실제 API의 타이밍과 실패 상황을 흉내내는 구현체.
 *
 * <p>실제 OpenAI로는 중단·타임아웃 같은 실패를 재현할 수 없다. 프론트의 중단 감지와
 * 재시도 로직을 검증할 유일한 수단이므로 OpenAI 연결 이후에도 삭제하지 않는다.
 *
 * <p>질문에 아래 키워드가 들어 있으면 해당 상황을 재현한다.
 * <ul>
 *   <li>{@code __slow__} 첫 토큰까지 10초 대기</li>
 *   <li>{@code __cut__} 절반쯤 보내다 아무 신호 없이 중단</li>
 *   <li>{@code __error__} 즉시 onError</li>
 *   <li>{@code __length__} finishReason "length"로 종료</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "chat", name = "client", havingValue = "mock", matchIfMissing = true)
public class MockChatCompletionClient implements ChatCompletionClient {

    private static final String TRIGGER_SLOW = "__slow__";
    private static final String TRIGGER_CUT = "__cut__";
    private static final String TRIGGER_ERROR = "__error__";
    private static final String TRIGGER_LENGTH = "__length__";

    /** 실제 API의 TTFT 재현. 로딩 UI 검증용이다. */
    private static final long FIRST_TOKEN_DELAY_MILLIS = 1_500L;
    private static final long SLOW_FIRST_TOKEN_DELAY_MILLIS = 10_000L;

    /** 토큰 간 간격. 렌더링 성능·깜빡임 확인용이다. */
    private static final long MIN_TOKEN_INTERVAL_MILLIS = 30L;
    private static final long MAX_TOKEN_INTERVAL_MILLIS = 50L;

    /** OpenAI도 단어 조각 단위로 보내므로 2~3글자로 쪼갠다. */
    private static final int MIN_TOKEN_LENGTH = 2;
    private static final int MAX_TOKEN_LENGTH = 3;

    private static final String ANSWER =
            "원서 접수 시에는 지원서와 자기소개서를 준비해 주세요. "
                    + "제출 서류는 접수 기간 내에 온라인으로 업로드하시면 됩니다. "
                    + "기간을 넘기면 접수가 불가능하니 미리 준비하시는 것을 권해드려요.";

    @Qualifier("chatStreamExecutor")
    private final Executor chatStreamExecutor;

    @Override
    public StreamSubscription stream(String systemPrompt, List<ChatMessage> messages, StreamHandler handler) {
        String question = lastUserContent(messages);
        AtomicBoolean cancelled = new AtomicBoolean(false);

        chatStreamExecutor.execute(() -> run(question, handler, cancelled));

        return () -> cancelled.set(true);
    }

    private void run(String question, StreamHandler handler, AtomicBoolean cancelled) {
        try {
            if (question.contains(TRIGGER_ERROR)) {
                handler.onError(new IllegalStateException("__error__ 트리거로 강제 발생시킨 실패입니다."));
                return;
            }

            long firstDelay = question.contains(TRIGGER_SLOW)
                    ? SLOW_FIRST_TOKEN_DELAY_MILLIS
                    : FIRST_TOKEN_DELAY_MILLIS;
            if (!sleep(firstDelay, cancelled)) {
                return;
            }

            boolean cut = question.contains(TRIGGER_CUT);
            int cutAt = ANSWER.length() / 2;

            int index = 0;
            while (index < ANSWER.length()) {
                if (cancelled.get()) {
                    log.debug("[CHAT] Mock 스트림이 취소되었습니다");
                    return;
                }
                if (cut && index >= cutAt) {
                    log.info("[CHAT] __cut__ 트리거로 완료 신호 없이 스트림을 중단합니다");
                    return;
                }
                int end = Math.min(index + tokenLength(), ANSWER.length());
                handler.onToken(ANSWER.substring(index, end));
                index = end;
                if (!sleep(tokenInterval(), cancelled)) {
                    return;
                }
            }

            handler.onComplete(question.contains(TRIGGER_LENGTH) ? "length" : "stop");
        } catch (RuntimeException e) {
            handler.onError(e);
        }
    }

    /**
     * @return 계속 진행해도 되면 {@code true}, 취소·인터럽트로 중단해야 하면 {@code false}
     */
    private boolean sleep(long millis, AtomicBoolean cancelled) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return !cancelled.get();
    }

    private int tokenLength() {
        return ThreadLocalRandom.current().nextInt(MIN_TOKEN_LENGTH, MAX_TOKEN_LENGTH + 1);
    }

    private long tokenInterval() {
        return ThreadLocalRandom.current().nextLong(MIN_TOKEN_INTERVAL_MILLIS, MAX_TOKEN_INTERVAL_MILLIS + 1);
    }

    private String lastUserContent(List<ChatMessage> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            ChatMessage message = messages.get(i);
            if (message.role() == ChatRole.USER) {
                return message.content();
            }
        }
        return "";
    }
}
