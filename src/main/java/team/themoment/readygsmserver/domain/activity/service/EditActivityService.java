package team.themoment.readygsmserver.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.activity.dto.request.ActivityReqDto;
import team.themoment.readygsmserver.domain.activity.dto.response.ActivityResDto;
import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsmserver.domain.activity.repository.ActivityRepository;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;
import team.themoment.sdk.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional
public class EditActivityService {

    private final ActivityRepository activityRepository;
    private final ApplicationRepository applicationRepository;

    public ActivityResDto execute(Long id, ActivityReqDto req) {
        ActivityJpaEntity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ExpectedException("활동을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        activity.update(req);
        return ActivityResDto.from(activity, applicationRepository.countByActivity_Id(id));
    }
}
