package team.themoment.readygsmserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import team.themoment.readygsmserver.domain.user.entity.constant.AuthReferrerType;
import team.themoment.readygsmserver.domain.user.entity.constant.Role;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_referrer_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private AuthReferrerType authReferrerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    public static UserJpaEntity buildMemberWithOauthInfo(String email, AuthReferrerType authReferrerType) {
        return UserJpaEntity.builder()
                .email(email)
                .authReferrerType(authReferrerType)
                .role(Role.USER)
                .lastLoginTime(LocalDateTime.now())
                .build();
    }

    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }
}
