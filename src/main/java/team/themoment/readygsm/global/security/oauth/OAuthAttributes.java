package team.themoment.readygsm.global.security.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class OAuthAttributes {

    private String email;
    private String name;
    private final Map<String, Object> attributes;

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        return OAuthAttributes.builder()
                .email(email)
                .name(name)
                .attributes(attributes)
                .build();
    }

    public UserJpaEntity toEntity() {
        return UserJpaEntity.builder()
                .email(email)
                .name(name)
                .role(UserRole.USER)
                .build();
    }
}
