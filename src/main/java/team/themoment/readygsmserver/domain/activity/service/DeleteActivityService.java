package team.themoment.readygsmserver.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsmserver.domain.activity.repository.ActivityRepository;
import team.themoment.sdk.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteActivityService {

    private final ActivityRepository activityRepository;

    public void execute(Long id) {
        ActivityJpaEntity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ExpectedException("활동을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        activityRepository.delete(activity);
    }
}
