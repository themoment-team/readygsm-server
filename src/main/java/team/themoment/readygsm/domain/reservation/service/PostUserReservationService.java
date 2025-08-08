package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.data.Activity;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;
import team.themoment.readygsm.domain.reservation.data.Reservation;
import team.themoment.readygsm.domain.reservation.presentation.data.PostUserReservationActivityDto;
import team.themoment.readygsm.domain.reservation.presentation.data.request.PostReservationReqDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostUserReservationResDto;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;
import team.themoment.readygsm.domain.user.data.User;
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
        Activity activity = activityJpaRepository.findById(activityId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.ACTIVITY_NOT_FOUND))
                .toDto();

        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.USER_NOT_FOUND))
                .toDto();

        /* 예약 인원 가득 차면 예외처리, 아니면 인원 수 증가 */
        activity.isFulledApplicant();

        Reservation savedReservation = reservationJpaRepository.save(
                Reservation.builder()
                        .activityId(activity)
                        .userId(user)
                        .phoneNumber(reqDto.phoneNumber())
                        .schoolName(reqDto.schoolName())
                        .grade(reqDto.grade())
                        .classNumber(reqDto.classNumber())
                        .studentNumber(reqDto.studentNumber())
                        .applicantName(reqDto.applicantName())
                        .build()
                        .toEntity()
        ).toDto();

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
