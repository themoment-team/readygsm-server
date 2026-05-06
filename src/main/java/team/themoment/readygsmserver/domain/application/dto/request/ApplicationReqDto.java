package team.themoment.readygsmserver.domain.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ApplicationReqDto(
        @NotBlank @Size(max = 10) String name,
        @Min(1) @Max(3) Integer grade,
        @Min(1) @Max(4) Integer classNumber,
        @Min(1) @Max(30) Integer number,
        @NotBlank @Size(max = 200) String schoolName,
        @NotBlank @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$") String phoneNumber,
        @NotBlank @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$") String familyPhoneNumber
) {
}
