package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;
import team.themoment.readygsm.domain.reservation.presentation.data.dto.PostUserReservationActivityDto;
import team.themoment.readygsm.domain.reservation.presentation.data.request.PostReservationReqDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostReservationResDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostUserReservationResDto;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;
import team.themoment.readygsm.domain.user.repository.UserJpaRepository;
import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional
public class PostUserReservationService {

    private final ActivityJpaRepository activityJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public PostUserReservationResDto postUserReservation(
            PostReservationReqDto reqDto,
            Long activityId,
            Long userId
    ) {
        ActivityJpaEntity activity = activityJpaRepository.findById(activityId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.ACTIVITY_NOT_FOUND));
        UserJpaEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND));

        /* 예약 인원 가득 차면 예외처리, 아니면 인원 수 증가 */
        activity.isFullorIncrease();

        ReservationJpaEntity savedReservation = reservationJpaRepository.save(
                ReservationJpaEntity.builder()
                        .activity(activity)
                        .user(user)
                        .phoneNumber(reqDto.phoneNumber())
                        .schoolName(reqDto.schoolName())
                        .grade(reqDto.grade())
                        .classNumber(reqDto.classNumber())
                        .studentNumber(reqDto.studentNumber())
                        .applicantName(reqDto.applicantName())
                        .build()
        );

        return new PostUserReservationResDto(
                savedReservation.getId(),
                new PostUserReservationActivityDto(
                        activityId,
                        activity.getName(),
                        activity.getImage(),
                        activity.getDate(),
                        activity.getCurrentApplicant(),
                        activity.getMaxApplicant(),
                        activity.getType(),
                        activity.getPlace(),
                        activity.getMajor(),
                        activity.getApplicationStart(),
                        activity.getApplicationEnd()
                )
        );
    }
}
