package team.themoment.readygsm.domain.reservation.presentation.data.request;

import jakarta.validation.constraints.NotNull;

public record PostReservationReqDto(
        @NotNull(message="전화번호는 필수입니다.") String phoneNumber,
        @NotNull(message="신청자 이름은 필수입니다.") String applicantName,
        String schoolName,
        Integer grade,
        Integer classNumber,
        Integer studentNumber
) {
}