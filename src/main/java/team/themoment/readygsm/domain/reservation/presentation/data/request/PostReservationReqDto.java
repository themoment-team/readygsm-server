package team.themoment.readygsm.domain.reservation.presentation.data.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record PostReservationReqDto(
        @NotNull String phoneNumber,
        @NotNull String applicantName,
        String schoolName,
        Integer grade,
        Integer classNumber,
        Integer studentNumber
) {
}