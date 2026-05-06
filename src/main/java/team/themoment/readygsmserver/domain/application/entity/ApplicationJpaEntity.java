package team.themoment.readygsmserver.domain.application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import team.themoment.readygsmserver.domain.activity.entity.ActivityJpaEntity;
import team.themoment.readygsmserver.domain.user.entity.UserJpaEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_application", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"activity_id", "user_id"}),
        @UniqueConstraint(columnNames = {"user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApplicationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private ActivityJpaEntity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "name", nullable = false, length = 10)
    private String name;

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Column(name = "class_number", nullable = false)
    private Integer classNumber;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "school_name", nullable = false, length = 200)
    private String schoolName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "family_phone_number", nullable = false, length = 20)
    private String familyPhoneNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}