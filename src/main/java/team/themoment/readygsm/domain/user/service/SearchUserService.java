package team.themoment.readygsm.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.reservation.data.Reservation;
import team.themoment.readygsm.domain.reservation.entity.ReservationJpaEntity;
import team.themoment.readygsm.domain.reservation.repository.ReservationJpaRepository;
import team.themoment.readygsm.domain.user.data.User;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;
import team.themoment.readygsm.domain.user.presentation.data.response.GetUserResDto;
import team.themoment.readygsm.domain.user.repository.UserJpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchUserService {

    private final UserJpaRepository userJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    public List<GetUserResDto> execute(
            String name,
            String email,
            UserRole role,
            int page,
            int limit
    ) {
        List<User> users = userJpaRepository.findByNameContainingAndEmailContainingAndRole(
                        name, email, role, PageRequest.of(page, limit)
                )
                .stream()
                .map(UserJpaEntity::toDto)
                .toList();
        List<Reservation> reservations = reservationJpaRepository.findByUserIdIn(
                        users.stream().map(User::getId).toList()
                )
                .stream()
                .map(ReservationJpaEntity::toDto)
                .toList();
        return users.stream()
                .map(user -> {
                    List<Reservation> userReservations = reservations.stream()
                            .filter(reservation -> reservation.getUserId().getId().equals(user.getId()))
                            .toList();
                    return new GetUserResDto(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            userReservations
                    );
                })
                .toList();
    }
}
