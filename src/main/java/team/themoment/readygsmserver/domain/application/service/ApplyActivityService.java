package team.themoment.readygsmserver.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsmserver.domain.activity.repository.ActivityRepository;
import team.themoment.readygsmserver.domain.application.dto.request.ApplicationReqDto;
import team.themoment.readygsmserver.domain.application.dto.response.ApplicationResDto;
import team.themoment.readygsmserver.domain.application.entity.ApplicationJpaEntity;
import team.themoment.readygsmserver.domain.application.repository.ApplicationRepository;
import team.themoment.readygsmserver.domain.user.entity.UserJpaEntity;
import team.themoment.readygsmserver.domain.user.repository.UserRepository;
import team.themoment.sdk.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplyActivityService {

    private final ApplicationRepository applicationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ApplicationResDto execute(Long userId, Long activityId, ApplicationReqDto req) {
        ActivityJpaEntity activity = activityRepository.findByIdWithLock(activityId)
                .orElseThrow(() -> new ExpectedException("활동을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (applicationRepository.existsByUser_Id(userId)) {
            throw new ExpectedException("이미 신청한 활동이 있습니다.", HttpStatus.CONFLICT);
        }

        long currentMainApplicants = applicationRepository.countByActivity_IdAndIsReserve(activityId, false);
        boolean hasReserve = applicationRepository.existsByActivity_IdAndIsReserve(activityId, true);
        boolean isReserve = currentMainApplicants >= activity.getMaxApplicant() || hasReserve;

        ApplicationJpaEntity saved = applicationRepository.save(
                ApplicationJpaEntity.builder()
                        .activity(activity)
                        .user(user)
                        .name(req.name())
                        .grade(req.grade())
                        .classNumber(req.classNumber())
                        .number(req.number())
                        .schoolName(req.schoolName())
                        .phoneNumber(req.phoneNumber())
                        .familyPhoneNumber(req.familyPhoneNumber())
                        .isReserve(isReserve)
                        .build()
        );

        return ApplicationResDto.from(saved);
    }
}
