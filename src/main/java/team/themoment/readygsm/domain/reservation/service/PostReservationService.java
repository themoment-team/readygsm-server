package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.data.Activity;
import team.themoment.readygsm.domain.activity.exception.ActivityNotFoundException;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;
import team.themoment.readygsm.domain.reservation.data.Reservation;
import team.themoment.readygsm.domain.reservation.presentation.data.request.PostReservationReqDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostReservationResDto;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;
import team.themoment.readygsm.domain.user.data.User;
import team.themoment.readygsm.domain.user.exception.UserNotFoundException;
import team.themoment.readygsm.domain.user.repository.UserJpaRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PostReservationService {
    private final ActivityJpaRepository activityJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public PostReservationResDto PostReservation(
            Long activityId,
            PostReservationReqDto reqDto) {
        Activity activity = activityJpaRepository.findById(activityId)
                .orElseThrow(ActivityNotFoundException::new)
                .toDto();

        // TODO:여기에 현재 로그인된 사용자의 user_id가 들어가도록 수정 필요
        Long userId = 1L;

        User user = userJpaRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new)
                .toDto();

        /* 예약 인원 가득 차면 예외처리, 아니면 인원 수 증가 */
        activity.increaseActivityApplicant();

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

        return new PostReservationResDto(
                savedReservation.getId(),
                reqDto.phoneNumber(),
                reqDto.schoolName(),
                reqDto.grade(),
                reqDto.classNumber(),
                reqDto.studentNumber(),
                reqDto.applicantName()
        );
    }
}