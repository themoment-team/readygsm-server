package team.themoment.readygsm.domain.reservation.presentation.data;

import team.themoment.readygsm.domain.activity.data.constant.ActivityType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PostUserReservationActivityDto(
        Long activityId,
        String activityName,
        String acitvityImage,
        LocalDate activityDate,
        Integer currentApplicant,
        Integer maxApplicant,
        ActivityType activityType,
        String activityPlace,
        String activityMajor,
        LocalDateTime applicationStart,
        LocalDateTime applicationEnd
) {
}