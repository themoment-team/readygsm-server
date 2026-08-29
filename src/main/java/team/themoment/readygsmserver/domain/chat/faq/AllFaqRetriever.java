package team.themoment.readygsmserver.domain.chat.faq;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FAQ 전체를 그대로 넘긴다.
 *
 * <p>30건 남짓이라 검색 단계를 두지 않는다. 검색이 없으면 검색 오류도 없다.
 */
@Component
public class AllFaqRetriever implements FaqRetriever {

    @Override
    public List<Faq> retrieve(String userQuestion) {
        return FaqCatalog.ALL;
    }
}
