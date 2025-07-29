package team.themoment.readygsm.global.security.oauth;

import lombok.Getter;
import team.themoment.readygsm.domain.auth.entity.AuthJpaEntity;
import team.themoment.readygsm.domain.user.data.constant.UserRole;

import java.util.Map;

@Getter
public class OAuthUserInfo {

    private final String email;
    private final String name;
    private final Map<String, Object> attributes;

    public OAuthUserInfo(Map<String, Object> attributes) {
        this.email = (String) attributes.get("email");
        this.name = (String) attributes.get("name");
        this.attributes = attributes;
    }

    public AuthJpaEntity toEntity() {
        return AuthJpaEntity.builder()
                .email(email)
                .name(name)
                .role(UserRole.USER) // 기본 권한
                .build();
    }

}
