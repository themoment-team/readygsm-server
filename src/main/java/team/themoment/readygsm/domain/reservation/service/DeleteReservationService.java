package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;
import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

@Service
@RequiredArgsConstructor
public class DeleteReservationService {
    private final ReservationJpaRepository reservationJpaRepository;

    public void deleteReservation(Long userId, Long reservationId) {
        ReservationJpaEntity reservationJpaEntity = reservationJpaRepository.findById(reservationId)
                .orElseThrow(() -> new ExpectedException(ErrorCode.RESERVATION_NOT_FOUND));

        if(!userId.equals(reservationJpaEntity.getUser().getId())) {
            throw new ExpectedException(ErrorCode.RESERVATION_FORBIDDEN);
        }

        reservationJpaRepository.delete(reservationJpaEntity);
    }
}
