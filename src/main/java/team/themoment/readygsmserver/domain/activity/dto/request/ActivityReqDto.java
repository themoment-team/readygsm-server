package team.themoment.readygsmserver.domain.activity.dto.request;

import java.time.LocalDateTime;

public record ActivityReqDto(
        String name,
        String place,
        String description,
        Integer maxApplicant,
        LocalDateTime activityDate,
        LocalDateTime start,
        LocalDateTime end
) {
}
