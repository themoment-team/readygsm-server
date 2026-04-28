package team.themoment.readygsmserver.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import team.themoment.readygsmserver.domain.auth.dto.request.OAuthCodeRequest;
import team.themoment.readygsmserver.domain.auth.service.OAuthAuthenticationService;
import team.themoment.readygsmserver.global.config.GoogleOAuthProperties;
import team.themoment.readygsmserver.global.security.oauth2.OAuth2Properties;

import java.io.IOException;

@RestController
@Tag(name = "Auth", description = "인증 API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthAuthenticationService oAuthAuthenticationService;
    private final GoogleOAuthProperties googleOAuthProperties;
    private final OAuth2Properties oauth2Properties;

    @Operation(summary = "Google OAuth 로그인 페이지로 이동", description = "Google 로그인 페이지로 리다이렉트합니다. redirect_uri를 지정하지 않으면 서버 기본값을 사용합니다.")
    @GetMapping("/google/redirect")
    public void redirectToGoogle(
            @RequestParam(required = false) String redirectUri,
            HttpServletResponse response) throws IOException {
        String state = oAuthAuthenticationService.generateState(redirectUri);

        String resolvedRedirectUri = redirectUri != null && !redirectUri.isBlank()
                ? redirectUri
                : googleOAuthProperties.redirectUri();

        String url = UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", googleOAuthProperties.clientId())
                .queryParam("redirect_uri", resolvedRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "email profile")
                .queryParam("state", state)
                .build().toUriString();
        response.sendRedirect(url);
    }

    @Operation(summary = "OAuth 로그인 (SPA용)", description = "프론트엔드에서 Authorization Code와 state를 전달해 로그인합니다.")
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

    @Operation(summary = "OAuth 콜백 (직접 리다이렉트용)", description = "Google이 직접 백엔드로 리다이렉트할 때 Authorization Code를 처리합니다.")
    @GetMapping("/{provider}")
    public void oauthCallback(
            @PathVariable String provider,
            @RequestParam String code,
            @RequestParam String state,
            HttpServletRequest httpRequest,
            HttpServletResponse response) throws IOException {
        oAuthAuthenticationService.execute(provider, code, state, httpRequest);
        response.sendRedirect(oauth2Properties.successRedirectUrl());
    }
}