package team.themoment.readygsmserver.domain.user.entity.constant;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    UNAUTHENTICATED,
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
