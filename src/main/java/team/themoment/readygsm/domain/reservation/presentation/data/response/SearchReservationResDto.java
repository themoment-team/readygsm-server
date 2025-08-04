package team.themoment.readygsm.domain.reservation.presentation.data.response;


import team.themoment.readygsm.domain.reservation.presentation.data.dto.SearchReservationAcitivityDto;

public record SearchReservationResDto(
        Long reservationId,
        String phoneNumber,
        String schoolName,
        Integer grade,
        Integer classNumber,
        Integer studentNumber,
        String applicantName,
        SearchReservationAcitivityDto activity
) {
}