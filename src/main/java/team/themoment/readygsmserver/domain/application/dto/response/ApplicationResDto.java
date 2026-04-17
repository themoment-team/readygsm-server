package team.themoment.readygsmserver.domain.application.dto.response;

import java.time.LocalDateTime;

public record ApplicationResDto(
        Long id,
        Long activityId,
        Long userId,
        String name,
        Integer grade,
        Integer classNumber,
        Integer number,
        String schoolName,
        String phoneNumber,
        String familyPhoneNumber
) {
}
