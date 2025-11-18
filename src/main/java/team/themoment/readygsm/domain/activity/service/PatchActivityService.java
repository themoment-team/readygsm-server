package team.themoment.readygsm.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.data.Activity;
import team.themoment.readygsm.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsm.domain.activity.exception.ActivityNotFoundException;
import team.themoment.readygsm.domain.activity.presentation.data.request.PatchActivityReqDto;
import team.themoment.readygsm.domain.activity.presentation.data.response.PatchActivityResDto;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PatchActivityService {

    private final ActivityJpaRepository activityJpaRepository;

    public PatchActivityResDto execute(
            final PatchActivityReqDto patchActivityReqDto,
            final Long activityId
    ) {
        ActivityJpaEntity activityJpaEntity = activityJpaRepository.findById(activityId)
                .orElseThrow(ActivityNotFoundException::new);
        activityJpaEntity.update(patchActivityReqDto);
        activityJpaRepository.save(activityJpaEntity);

        Activity activity = activityJpaEntity.toDto();
        return new PatchActivityResDto(
                activity.getId(),
                activity.getName(),
                activity.getDate(),
                activity.getCurrentApplicant(),
                activity.getMaxApplicant(),
                activity.getType(),
                activity.getPlace(),
                activity.getMajor(),
                activity.getApplicationStart(),
                activity.getApplicationEnd(),
                ActivityJpaEntity.getApplicationStatus(
                        activity.getApplicationStart(),
                        activity.getApplicationEnd()
                )
        );
    }
}
