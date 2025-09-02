package team.themoment.readygsm.domain.reservation.service;

import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.exception.ActivityNotFoundException;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;
import team.themoment.readygsm.domain.reservation.data.Reservation;
import team.themoment.readygsm.domain.reservation.exception.ReservationNonUniqueException;
import team.themoment.readygsm.domain.reservation.exception.ReservationNotFoundException;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteReservationService {
    private final ReservationJpaRepository reservationJpaRepository;
    private final ActivityJpaRepository activityJpaRepository;

    public void deleteReservation(Long reservationId) {
        // TODO:여기에 현재 로그인된 사용자의 user_id가 들어가도록 수정 필요
        // 활동 인원 감소와 예약 삭제 기능 구현
        Long userId = 1L;

        Reservation reservation;
        try {
            reservation = reservationJpaRepository.findByReservationIdAndUserId(reservationId, userId).toDto();
        } catch (EmptyResultDataAccessException e) {
            throw new ReservationNotFoundException();
        } catch (NonUniqueResultException e) {
            throw new ReservationNonUniqueException();
        }

        Long activityId = reservation.getActivityId().getId();
        int amount = activityJpaRepository.decreaseActivityApplicant(activityId);
        if(amount == 0) {
            throw new ActivityNotFoundException();
        }

        reservationJpaRepository.deleteById(reservationId);
    }
}