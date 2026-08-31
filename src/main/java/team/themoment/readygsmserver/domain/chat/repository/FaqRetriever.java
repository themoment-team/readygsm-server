package team.themoment.readygsmserver.domain.chat.repository;

import team.themoment.readygsmserver.domain.chat.entity.Faq;

import java.util.List;

public interface FaqRetriever {

    /**
     * 프롬프트에 실을 FAQ를 가져온다.
     *
     * <p>{@code userQuestion}은 현재 구현에서 쓰이지 않는다. FAQ가 늘어 검색 기반
     * 구현체를 추가할 때 인터페이스를 바꾸지 않으려고 미리 시그니처에 둔 것이다.
     */
    List<Faq> retrieve(String userQuestion);
}
