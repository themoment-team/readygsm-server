package team.themoment.readygsmserver.domain.activity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.activity.dto.request.ActivityReqDto;
import team.themoment.readygsmserver.domain.activity.dto.response.ActivityResDto;
import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsmserver.domain.activity.repository.ActivityRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateActivityService {

    private final ActivityRepository activityRepository;

    public ActivityResDto execute(ActivityReqDto req) {
        ActivityJpaEntity saved = activityRepository.save(
                ActivityJpaEntity.builder()
                        .name(req.name())
                        .place(req.place())
                        .description(req.description())
                        .maxApplicant(req.maxApplicant())
                        .activityDate(req.activityDate())
                        .start(req.start())
                        .end(req.end())
                        .build()
        );
        return ActivityResDto.from(saved);
    }
}
