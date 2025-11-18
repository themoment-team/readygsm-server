package team.themoment.readygsm.global.security.oauth;

import lombok.Getter;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;

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



    public UserJpaEntity toEntity() {
        return UserJpaEntity.builder()
                .email(email)
                .name(name)
                .role(UserRole.USER)
                .build();
    }
}

