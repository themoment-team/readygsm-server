package team.themoment.readygsm.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;
import team.themoment.readygsm.domain.user.exception.UserNotFoundException;
import team.themoment.readygsm.domain.user.presentation.data.response.GetUserResDto;
import team.themoment.readygsm.domain.user.repository.UserJpaRepository;

@Service
@RequiredArgsConstructor
public class FindUserService {

    private final UserJpaRepository userJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    public GetUserResDto execute(Long userId) {
        return userJpaRepository.findById(userId)
                .map(user -> new GetUserResDto(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        reservationJpaRepository.findByUserId(userId)
                                .stream()
                                .map(ReservationJpaEntity::toDto)
                                .toList()
                ))
                .orElseThrow(UserNotFoundException::new);
    }
}