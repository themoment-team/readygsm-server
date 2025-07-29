package team.themoment.readygsm.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class AuthResponse {
    private Long userId;
    private String token;
    private LocalDateTime tokenExpiresAt;
    private LocalDateTime tokenIssuedAt;
    private String role;
}