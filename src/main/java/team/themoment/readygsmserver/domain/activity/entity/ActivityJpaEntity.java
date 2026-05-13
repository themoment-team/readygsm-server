package team.themoment.readygsmserver.domain.activity.entity;

import jakarta.persistence.*;
import lombok.*;
import team.themoment.readygsmserver.domain.activity.dto.request.ActivityReqDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tb_activity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ActivityJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "place", nullable = false, length = 200)
    private String place;

    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @Column(name = "max_applicant", nullable = false)
    private Integer maxApplicant;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "registration_start_at", nullable = false)
    private LocalDateTime registrationStartAt;

    @Column(name = "registration_end_at", nullable = false)
    private LocalDateTime registrationEndAt;

    @Column(name = "activity_start_time", nullable = false)
    private LocalTime activityStartTime;

    @Column(name = "activity_end_time", nullable = false)
    private LocalTime activityEndTime;

    public void update(ActivityReqDto req) {
        this.name = req.name();
        this.place = req.place();
        this.description = req.description();
        this.maxApplicant = req.maxApplicant();
        this.activityDate = req.activityDate();
        this.registrationStartAt = req.registrationStartAt();
        this.registrationEndAt = req.registrationEndAt();
        this.activityStartTime = req.activityStartTime();
        this.activityEndTime = req.activityEndTime();
    }
}
