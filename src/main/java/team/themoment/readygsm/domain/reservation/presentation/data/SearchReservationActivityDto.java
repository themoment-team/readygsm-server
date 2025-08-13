package team.themoment.readygsm.domain.reservation.presentation.data;

import team.themoment.readygsm.domain.activity.data.constant.ActivityType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SearchReservationActivityDto(
        Long activityId,
        String activityName,
        String activityImage,
        LocalDate activityDate,
        Integer currentApplicant,
        Integer maxApplicant,
        ActivityType activityType,
        String activityPlace,
        String activityMajor,
        LocalDateTime applicationStart,
        LocalDateTime applicationEnd,
        String applicationStatus
) {
}
