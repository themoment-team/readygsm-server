package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;
import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteReservationService {
    private final ReservationJpaRepository reservationJpaRepository;
    private final ActivityJpaRepository activityJpaRepository;

    public void deleteReservation(Long reservationId) {

        // TODO:여기에 현재 로그인된 사용자의 user_id가 들어가도록 수정 필요
        Long userId = 1L;

        int deletedCount = reservationJpaRepository.deleteByIdAndUserId(userId, reservationId);
        if(deletedCount == 0) {
            if(reservationJpaRepository.existsById(reservationId)) {
                throw new ExpectedException(ErrorCode.RESERVATION_NOT_FOUND);
            } else {
                throw new ExpectedException(ErrorCode.RESERVATION_FORBIDDEN);
            }
        }

        Long activityId = reservationJpaRepository.findById(reservationId).get().getActivity().getId();
        if(activityJpaRepository.existsById(activityId)) {
            activityJpaRepository.decreaseActivityApplicant(activityId);
        }
    }
}
