package team.themoment.readygsm.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.data.Activity;
import team.themoment.readygsm.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsm.domain.activity.presentation.data.response.SearchActivityResDto;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchActivityService {

    private final ActivityJpaRepository activityJpaRepository;

    public List<SearchActivityResDto> execute(
            final String activityName,
            final int page,
            final int limit
    ) {
        Page<Activity> searchResult = activityJpaRepository.findByNameWithPaging(
                activityName,
                PageRequest.of(page, limit)
        ).map(ActivityJpaEntity::toDto);

        return searchResult.getContent().stream()
                .map(a -> new SearchActivityResDto(
                        a.getId(),
                        a.getName(),
                        a.getDate(),
                        a.getCurrentApplicant(),
                        a.getMaxApplicant(),
                        a.getType(),
                        a.getPlace(),
                        a.getMajor(),
                        a.getApplicationStart(),
                        a.getApplicationEnd(),
                        ActivityJpaEntity.getApplicationStatus(
                                a.getApplicationStart(),
                                a.getApplicationEnd()
                        )
                )).toList();
    }
}
