package team.themoment.readygsm.domain.reservation.presentation.data.response;

public record PostReservationResDto(
        Long reservationId,
        String phoneNumber,
        String schoolName,
        Integer grade,
        Integer classNumber,
        Integer studentNumber,
        String applicantName
) {
}
