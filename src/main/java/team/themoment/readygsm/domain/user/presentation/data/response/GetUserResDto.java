package team.themoment.readygsm.domain.user.presentation.data.response;

import team.themoment.readygsm.domain.reservation.data.Reservation;
import team.themoment.readygsm.domain.user.data.constant.UserRole;

import java.util.List;

public record GetUserResDto(
        Long userId,
        String name,
        String email,
        UserRole role,
        List<Reservation> reservations
) {
}