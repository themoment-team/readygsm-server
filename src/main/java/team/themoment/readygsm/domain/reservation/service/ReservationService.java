package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;
import team.themoment.readygsm.domain.reservation.data.Reservation;
import team.themoment.readygsm.domain.reservation.presentation.data.request.PostReservationReqDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostReservationResDto;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;
import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ActivityJpaRepository activityJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    /* 활동 예약 */
    public PostReservationResDto PostReservation(
            Long userId,
            Long activityId,
            PostReservationReqDto reqDto) {
        if(!(activityJpaRepository.findById(activityId).isPresent())) {
            throw new ExpectedException(ErrorCode.ACTIVITY_NOT_FOUND);
        }
        reservationJpaRepository.save();
    }
}