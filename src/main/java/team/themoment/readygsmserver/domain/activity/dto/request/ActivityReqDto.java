package team.themoment.readygsmserver.domain.activity.dto.request;

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
}
