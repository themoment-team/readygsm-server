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
import team.themoment.readygsmserver.domain.user.entity.constant.Role;
import team.themoment.readygsmserver.domain.user.repository.UserRepository;
import team.themoment.readygsmserver.global.discord.DiscordNotificationService;
import team.themoment.sdk.exception.ExpectedException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplyActivityService {

    private static final int MAX_RESERVE_APPLICANT = 3;

    private final ApplicationRepository applicationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final DiscordNotificationService discordNotificationService;

    public ApplicationResDto execute(Long userId, Long activityId, ApplicationReqDto req) {
        ActivityJpaEntity activity = activityRepository.findByIdWithLock(activityId)
                .orElseThrow(() -> new ExpectedException("활동을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        UserJpaEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (user.getRole() == Role.USER) {
            LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
            if (now.isBefore(activity.getRegistrationStartAt()) || now.isAfter(activity.getRegistrationEndAt())) {
                throw new ExpectedException("신청 기간이 아닙니다.", HttpStatus.BAD_REQUEST);
            }
        }

        if (applicationRepository.existsByUser_Id(userId)) {
            throw new ExpectedException("이미 신청한 활동이 있습니다.", HttpStatus.CONFLICT);
        }

        long currentMainApplicants = applicationRepository.countByActivity_IdAndIsReserve(activityId, false);
        long currentReserveApplicants = applicationRepository.countByActivity_IdAndIsReserve(activityId, true);
        boolean isReserve = currentMainApplicants >= activity.getMaxApplicant() || currentReserveApplicants > 0;

        if (isReserve && currentReserveApplicants >= MAX_RESERVE_APPLICANT) {
            throw new ExpectedException("예비인원이 마감되었습니다.", HttpStatus.CONFLICT);
        }

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

        long newMainApplicants = isReserve ? currentMainApplicants : currentMainApplicants + 1;
        long newReserveApplicants = isReserve ? currentReserveApplicants + 1 : currentReserveApplicants;
        discordNotificationService.sendApplicationCreated(
                activity.getName(), activity.getId(),
                req.name(), req.schoolName(),
                newMainApplicants, newReserveApplicants);

        Integer reserveOrder = isReserve ? (int) currentReserveApplicants + 1 : null;
        return ApplicationResDto.from(saved, reserveOrder);
    }
}
