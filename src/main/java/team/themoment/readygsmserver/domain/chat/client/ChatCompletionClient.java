package team.themoment.readygsmserver.domain.chat.client;

import team.themoment.readygsmserver.domain.chat.entity.ChatMessage;

import java.util.List;

/**
 * AI 호출부. 구현체를 갈아끼워도 컨트롤러·DTO·SSE 로직은 바뀌지 않는다.
 */
public interface ChatCompletionClient {

    /**
     * 토큰을 순차적으로 흘려보낸다. 완료·실패는 {@code handler}로 알린다.
     *
     * @param systemPrompt 지침과 FAQ 전문. 프롬프트 캐싱을 위해 매 요청 동일해야 한다
     * @param messages     system을 제외한 대화 이력. 마지막 항목이 이번 질문이다
     * @return 연결이 끊겼을 때 upstream을 취소할 손잡이
     */
    StreamSubscription stream(String systemPrompt, List<ChatMessage> messages, StreamHandler handler);
}
