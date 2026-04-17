package team.themoment.readygsmserver.domain.activity.dto.response;

import java.time.LocalDateTime;

public record ActivityResDto(
        Long id,
        String name,
        String place,
        String description,
        Integer maxApplicant,
        LocalDateTime activityDate,
        LocalDateTime start,
        LocalDateTime end
) {
}
