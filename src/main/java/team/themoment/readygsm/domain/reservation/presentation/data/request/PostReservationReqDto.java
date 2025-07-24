package team.themoment.readygsm.domain.reservation.presentation.data.request;

import jakarta.annotation.Nullable;

public record PostReservationReqDto(
        String phoneNumber,
        String applicantName,
        String schoolName,
        Integer grade,
        Integer classNumber,
        Integer studentNumber
) {
}