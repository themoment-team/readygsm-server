package team.themoment.readygsm.domain.activity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.themoment.readygsm.domain.activity.data.Activity;
import team.themoment.readygsm.domain.activity.data.constant.ActivityType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "activity")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ActivityJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;
    @Column(name = "activity_name", nullable = false)
    private String name;
    @Column(name = "activity_image")
    private String image;
    @Column(name = "activity_current_applicant", columnDefinition = "integer default 0")
    private Integer currentApplicant;
    @Column(name = "activity_max_applicant")
    private Integer maxApplicant;
    @Column(name = "activity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType type;
    @Column(name = "activity_date", nullable = false)
    private LocalDate date;
    @Column(name = "activity_place", nullable = false)
    private String place;
    @Column(name = "activity_major")
    private String major;
    @Column(name = "activity_application_start", nullable = false)
    private LocalDateTime applicationStart;
    @Column(name = "activity_application_end", nullable = false)
    private LocalDateTime applicationEnd;

    public Activity toDto() {
        return Activity.builder()
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