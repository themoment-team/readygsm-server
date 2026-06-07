package team.themoment.readygsmserver.domain.activity.dto.response;

import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ActivityResDto(
        Long id,
        String name,
        String place,
        String description,
        Integer maxApplicant,
        Long currentApplicant,
        LocalDate activityDate,
        LocalDateTime registrationStartAt,
        LocalDateTime registrationEndAt,
        LocalTime activityStartTime,
        LocalTime activityEndTime
) {
    public static ActivityResDto from(ActivityJpaEntity entity, long currentApplicant) {
        return new ActivityResDto(
                entity.getId(),
                entity.getName(),
                entity.getPlace(),
                entity.getDescription(),
                entity.getMaxApplicant(),
                currentApplicant,
                entity.getActivityDate(),
                entity.getRegistrationStartAt(),
                entity.getRegistrationEndAt(),
                entity.getActivityStartTime(),
                entity.getActivityEndTime()
        );
    }
}
