package team.themoment.readygsm.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import team.themoment.readygsm.domain.user.data.constant.UserRole;

import java.time.LocalDateTime;

@Builder
@Getter
public class AuthResponse {
    private Long userId;
    private String token;
    private Long tokenExpiresAt;
    private Long tokenIssuedAt;
    private UserRole role;
}