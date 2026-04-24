package team.themoment.readygsmserver.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import team.themoment.readygsmserver.domain.auth.dto.request.OAuthCodeRequest;
import team.themoment.readygsmserver.domain.auth.service.OAuthAuthenticationService;
import team.themoment.readygsmserver.global.config.GoogleOAuthProperties;

import java.io.IOException;
import java.util.UUID;

@RestController
@Tag(name = "Auth", description = "인증 API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthAuthenticationService oAuthAuthenticationService;
    private final GoogleOAuthProperties googleOAuthProperties;

    @Operation(summary = "Google OAuth 로그인 페이지로 이동", description = "Google 로그인 페이지로 리다이렉트합니다.")
    @GetMapping("/google/redirect")
    public void redirectToGoogle(HttpServletResponse response, HttpSession session) throws IOException {
        String state = UUID.randomUUID().toString();
        session.setAttribute(OAuthAuthenticationService.OAUTH2_STATE_SESSION_KEY, state);

        String url = UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", googleOAuthProperties.clientId())
                .queryParam("redirect_uri", googleOAuthProperties.redirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
                .queryParam("state", state)
                .build().toUriString();
        response.sendRedirect(url);
    }

    @Operation(summary = "OAuth 로그인", description = "OAuth Provider로부터 받은 Authorization Code로 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 인가 코드 또는 state"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/{provider}")
    public ResponseEntity<Void> oauthLogin(
            @PathVariable String provider,
            @RequestBody OAuthCodeRequest request,
            HttpServletRequest httpRequest) {
        oAuthAuthenticationService.execute(provider, request.code(), request.state(), httpRequest);
        return ResponseEntity.ok().build();
    }
}