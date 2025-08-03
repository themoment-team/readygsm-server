package team.themoment.readygsm.domain.reservation.presentation.data.response;

import team.themoment.readygsm.domain.reservation.presentation.data.dto.PostUserReservationActivityDto;

public record PostUserReservationResDto(
        Long reservationId,
        PostUserReservationActivityDto activityInfo
) {
}
