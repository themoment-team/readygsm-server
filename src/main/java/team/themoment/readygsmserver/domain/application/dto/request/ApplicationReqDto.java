package team.themoment.readygsmserver.domain.application.dto.request;

public record ApplicationReqDto(
        String name,
        Integer grade,
        Integer classNumber,
        Integer number,
        String schoolName,
        String phoneNumber,
        String familyPhoneNumber
) {
}
