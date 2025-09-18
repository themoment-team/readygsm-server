package team.themoment.readygsm.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.exception.ActivityNotFoundException;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteActivityService {

    private final ActivityJpaRepository activityJpaRepository;

    public void deleteActivity(
            final Long activityId
    ) {
        activityJpaRepository.findById(activityId)
                .orElseThrow(ActivityNotFoundException::new);
        activityJpaRepository.deleteById(activityId);
    }
}
