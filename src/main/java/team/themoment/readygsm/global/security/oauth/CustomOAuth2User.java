package team.themoment.readygsm.global.security.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import team.themoment.readygsm.domain.user.data.constant.UserRole;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final Long userId;
    private final String email;
    private final String name;
    private final UserRole role;

    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            Long userId,
            String email,
            String name,
            UserRole role
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }

}
