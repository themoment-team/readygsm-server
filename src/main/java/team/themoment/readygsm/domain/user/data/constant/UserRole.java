package team.themoment.readygsm.domain.user.data.constant;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    TEACHER("ROLE_TEACHER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}