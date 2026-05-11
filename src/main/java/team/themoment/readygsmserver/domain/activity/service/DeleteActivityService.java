package team.themoment.readygsmserver.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.activity.repository.ActivityRepository;
import team.themoment.sdk.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteActivityService {

    private final ActivityRepository activityRepository;

    public void execute(Long id) {
        int deleted = activityRepository.deleteByActivityId(id);
        if (deleted == 0) {
            throw new ExpectedException("활동을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
}
