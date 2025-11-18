package team.themoment.readygsm.global.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.TokenResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team.themoment.readygsm.domain.auth.dto.AuthResponse;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.global.security.jwt.JwtProvider;

import java.io.IOException;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customUser.getUserId();
        UserRole role = customUser.getRole();

        String token = jwtProvider.createToken(userId, role);

        long issuedAtEpoch = jwtProvider.getTokenIssuedAtLocalDateTime(token)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        long expiresAtEpoch = jwtProvider.getTokenExpirationLocalDateTime(token)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        AuthResponse tokenResponse = AuthResponse.builder()
                .userId(userId)
                .token(token)
                .tokenExpiresAt(expiresAtEpoch)
                .tokenIssuedAt(issuedAtEpoch)
                .role(role)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(tokenResponse);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}

