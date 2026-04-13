package team.themoment.readygsmserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import team.themoment.readygsmserver.domain.user.entity.constant.Role;

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

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;
}
