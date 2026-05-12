package team.themoment.readygsmserver.domain.activity.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ActivityReqDto(
        @NotBlank @Size(max = 256) String name,
        @NotBlank @Size(max = 200) String place,
        @NotBlank @Size(max = 512) String description,
        @NotNull @Min(1) Integer maxApplicant,
        @NotNull LocalDate activityDate,
        @NotNull LocalDateTime registrationStartAt,
        @NotNull LocalDateTime registrationEndAt,
        @NotNull LocalTime activityStartTime,
        @NotNull LocalTime activityEndTime
) {
    @AssertTrue(message = "신청 시작 시각은 종료 시각보다 이전이어야 합니다.")
    public boolean isRegistrationStartBeforeEnd() {
        if (registrationStartAt == null || registrationEndAt == null) return true;
        return registrationStartAt.isBefore(registrationEndAt);
    }

    @AssertTrue(message = "활동 시작 시각은 종료 시각보다 이전이어야 합니다.")
    public boolean isActivityStartBeforeEnd() {
        if (activityStartTime == null || activityEndTime == null) return true;
        return activityStartTime.isBefore(activityEndTime);
    }

    @AssertTrue(message = "신청 마감은 활동 시작 시각보다 이전이어야 합니다.")
    public boolean isRegistrationEndBeforeActivity() {
        if (registrationEndAt == null || activityDate == null || activityStartTime == null) return true;
        return registrationEndAt.isBefore(LocalDateTime.of(activityDate, activityStartTime));
    }
}
