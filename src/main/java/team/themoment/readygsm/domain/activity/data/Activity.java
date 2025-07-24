package team.themoment.readygsm.domain.activity.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.themoment.readygsm.domain.activity.data.constant.ActivityType;
import team.themoment.readygsm.domain.activity.entity.ActivityJpaEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    private Long id;
    private String name;
    private String image;
    private Integer currentApplicant;
    private Integer maxApplicant;
    private ActivityType type;
    private LocalDate date;
    private String place;
    private String major;
    private LocalDateTime applicationStart;
    private LocalDateTime applicationEnd;

    public ActivityJpaEntity toEntity() {
        return ActivityJpaEntity.builder()
                .id(id)
                .name(name)
                .image(image)
                .currentApplicant(currentApplicant)
                .maxApplicant(maxApplicant)
                .type(type)
                .date(date)
                .place(place)
                .major(major)
                .applicationStart(applicationStart)
                .applicationEnd(applicationEnd)
                .build();
    }
}