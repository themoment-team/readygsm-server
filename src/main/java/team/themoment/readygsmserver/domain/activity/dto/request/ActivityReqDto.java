package team.themoment.readygsmserver.domain.activity.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActivityReqDto(
        @NotBlank @Size(max = 256) String name,
        @NotBlank @Size(max = 200) String place,
        @NotBlank @Size(max = 512) String description,
        @NotNull @Min(1) Integer maxApplicant,
        @NotNull LocalDate activityDate,
        @NotNull LocalDate start,
        @NotNull LocalDate end
) {
    @AssertTrue(message = "신청 시작일은 종료일보다 이전이어야 합니다.")
    public boolean isStartBeforeEnd() {
        if (start == null || end == null) return true;
        return !start.isAfter(end);
    }

    @AssertTrue(message = "활동일은 신청 시작일 이후여야 합니다.")
    public boolean isActivityDateAfterStart() {
        if (activityDate == null || start == null) return true;
        return !activityDate.isBefore(start);
    }
}
