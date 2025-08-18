package team.themoment.readygsm.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.activity.repository.ActivityJpaRepository;
import team.themoment.readygsm.domain.reservation.exception.ReservationForbiddenException;
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
        Long userId = 1L;

        Long activityId = reservationJpaRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new)
                .getActivity().getId();

        int deletedCount = reservationJpaRepository.deleteByIdAndUserId(userId, reservationId);
        if(deletedCount == 0) {
            throw new ReservationForbiddenException();
        }

        if(activityJpaRepository.existsById(activityId)) {
            activityJpaRepository.decreaseActivityApplicant(activityId);
        }
    }
}
