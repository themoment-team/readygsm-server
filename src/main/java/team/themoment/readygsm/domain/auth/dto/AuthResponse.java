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
    private LocalDateTime tokenExpiresAt;
    private LocalDateTime tokenIssuedAt;
    private UserRole role;
}