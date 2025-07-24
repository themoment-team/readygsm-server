package team.themoment.readygsm.domain.user.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String email;
    private String name;
    private UserRole role;

    public UserJpaEntity toEntity() {
        return UserJpaEntity.builder()
                .id(id)
                .email(email)
                .name(name)
                .role(role)
                .build();
    }
}
