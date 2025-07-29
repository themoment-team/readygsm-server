package team.themoment.readygsm.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AuthJpaEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private String name;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
