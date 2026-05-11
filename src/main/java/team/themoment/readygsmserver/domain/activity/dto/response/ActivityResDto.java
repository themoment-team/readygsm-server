package team.themoment.readygsmserver.domain.activity.dto.response;

import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;

import java.time.LocalDate;

public record ActivityResDto(
        Long id,
        String name,
        String place,
        String description,
        Integer maxApplicant,
        LocalDate activityDate,
        LocalDate start,
        LocalDate end
) {
    public static ActivityResDto from(ActivityJpaEntity entity) {
        return new ActivityResDto(
                entity.getId(),
                entity.getName(),
                entity.getPlace(),
                entity.getDescription(),
                entity.getMaxApplicant(),
                entity.getActivityDate(),
                entity.getStart(),
                entity.getEnd()
        );
    }
}
