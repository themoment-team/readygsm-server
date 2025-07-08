package team.themoment.readygsm.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.themoment.readygsm.domain.user.data.constant.UserRole;

@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserJpaEntity toDto() {
        return UserJpaEntity.builder()
                .id(id)
                .email(email)
                .name(name)
                .role(role)
                .build();
    }
}