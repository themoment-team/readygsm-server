package team.themoment.readygsm.domain.activity.presentation.data.response;

import team.themoment.readygsm.domain.activity.data.constant.ActivityType;
import team.themoment.readygsm.domain.reservation.data.constant.ActivityStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatchActivityResDto(
        Long activityId,
        String activityName,
        LocalDate activityDate,
        Integer currentApplicant,
        Integer maxApplicant,
        ActivityType activityType,
        String activityPlace,
        String activityMajor,
        LocalDateTime applicationStart,
        LocalDateTime applicationEnd,
        ActivityStatus applicationStatus
) {
}
