package team.themoment.readygsm.global.security.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final UserJpaEntity user;
    private final Map<String, Object> attributes;
    private final String jwt;

    public CustomOAuth2User(UserJpaEntity user, Map<String, Object> attributes, String jwt) {
        this.user = user;
        this.attributes = attributes;
        this.jwt = jwt;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }

    public Long getId() {
        return user.getId();
    }

    public UserRole getRole() {
        return user.getRole();
    }

    public String getJwt() {
        return jwt;
    }

    public UserJpaEntity getUser() {
        return user;
    }
}

