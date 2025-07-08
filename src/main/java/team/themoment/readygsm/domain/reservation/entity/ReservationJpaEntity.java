package team.themoment.readygsm.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.themoment.readygsm.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;

@Table(name = "reservation")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReservationJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private ActivityJpaEntity activity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name="school_name", nullable = false)
    private String schoolName;
    @Column(name = "grade", nullable = false)
    private Integer grade;
    @Column(name = "class_number", nullable = false)
    private Integer classNumber;
    @Column(name = "student_number", nullable = false)
    private Integer studentNumber;
    @Column(name="applicant_name", nullable = false)
    private String applicantName;
}
