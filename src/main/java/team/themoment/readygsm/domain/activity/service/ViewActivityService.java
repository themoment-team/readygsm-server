package team.themoment.readygsm.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.data.Activity;
import team.themoment.readygsm.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsm.domain.activity.exception.ActivityNotFoundException;
import team.themoment.readygsm.domain.activity.presentation.data.response.ViewActivityResDto;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class ViewActivityService {

    private final ActivityJpaRepository activityJpaRepository;

    public ViewActivityResDto viewActivity(
            final Long activityId
    ) {
        Activity foundActivity = activityJpaRepository.findById(activityId)
                .orElseThrow(ActivityNotFoundException::new)
                .toDto();

        return new ViewActivityResDto(
                activityId,
                foundActivity.getName(),
                foundActivity.getDate(),
                foundActivity.getCurrentApplicant(),
                foundActivity.getMaxApplicant(),
                foundActivity.getType(),
                foundActivity.getPlace(),
                foundActivity.getMajor(),
                foundActivity.getApplicationStart(),
                foundActivity.getApplicationEnd(),
                ActivityJpaEntity.getApplicationStatus(
                        foundActivity.getApplicationStart(),
                        foundActivity.getApplicationEnd()
                )
        );
    }
}
