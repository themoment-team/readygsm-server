package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;
import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteReservationService {
    private final ReservationJpaRepository reservationJpaRepository;

    public void deleteReservation(Long userId, Long reservationId) {
        int deletedCount = reservationJpaRepository.deleteByIdAndUserId(userId, reservationId);
        if(deletedCount == 0) {
            if(reservationJpaRepository.existsById(reservationId)) {
                throw new ExpectedException(ErrorCode.RESERVATION_NOT_FOUND);
            } else {
                throw new ExpectedException(ErrorCode.RESERVATION_FORBIDDEN);
            }
        }
    }
}
