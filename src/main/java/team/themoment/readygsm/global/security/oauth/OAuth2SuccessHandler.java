package team.themoment.readygsm.global.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.global.security.jwt.JwtProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException{

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = oAuth2User.getId();
        UserRole role = oAuth2User.getRole();

        String token = jwtProvider.createToken(userId, role);

        // JWT 응답 JSON
        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("userId", userId);
        tokenResponse.put("token", token);
        tokenResponse.put("role", role.name());
        tokenResponse.put("tokenIssuedAt", ZonedDateTime.now().toString());
        tokenResponse.put("tokenExpiresAt", ZonedDateTime.now().plusSeconds(60 * 60).toString());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
    }
}
