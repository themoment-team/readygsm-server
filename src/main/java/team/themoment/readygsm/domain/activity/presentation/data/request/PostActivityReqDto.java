package team.themoment.readygsm.domain.activity.presentation.data.request;

import jakarta.validation.constraints.NotNull;
import team.themoment.readygsm.domain.activity.data.constant.ActivityType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PostActivityReqDto(
        @NotNull(message = "활동명은 필수입니다.") String activityName,
        @NotNull(message = "활동 일시는 필수입니다.") LocalDate activityDate,
        @NotNull(message = "최대 활동 인원은 필수입니다.") Integer maxApplicant,
        @NotNull(message = "활동 종류는 필수입니다.") ActivityType activityType,
        @NotNull(message = "활동 장소는 필수입니다.") String activityPlace,
        @NotNull(message = "활동 전공은 필수입니다.") String activityMajor,
        @NotNull(message = "활동 신청 시작 일시는 필수입니다.") LocalDateTime applicationStart,
        @NotNull(message = "활동 신청 마감 일시는 필수입니다.") LocalDateTime applicationEnd
) {
}
