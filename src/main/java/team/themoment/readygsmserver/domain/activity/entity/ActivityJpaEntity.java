package team.themoment.readygsmserver.domain.activity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "start", nullable = false)
    private LocalDate start;

    @Column(name = "end", nullable = false)
    private LocalDate end;
}
