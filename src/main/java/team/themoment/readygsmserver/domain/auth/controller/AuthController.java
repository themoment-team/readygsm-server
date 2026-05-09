package team.themoment.readygsmserver.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.themoment.readygsmserver.domain.auth.dto.request.OAuthCodeRequest;
import team.themoment.readygsmserver.domain.auth.service.LogoutService;
import team.themoment.readygsmserver.domain.auth.service.OAuthAuthenticationService;
import team.themoment.sdk.response.CommonApiResponse;

@RestController
@Tag(name = "Auth", description = "인증 API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthAuthenticationService oAuthAuthenticationService;
    private final LogoutService logoutService;

    @Operation(summary = "로그아웃", description = "현재 세션을 만료시킵니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 완료")
    @PostMapping("/logout")
    public CommonApiResponse logout(HttpServletRequest request) {
        logoutService.execute(request);
        return CommonApiResponse.success("로그아웃 완료");
    }

    @Operation(summary = "OAuth 로그인", description = "프론트엔드에서 Authorization Code와 redirect_uri를 전달해 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 인가 코드 또는 허용되지 않은 redirect_uri"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/{provider}")
    public ResponseEntity<Void> oauthLogin(
            @PathVariable String provider,
            @RequestBody OAuthCodeRequest request,
            HttpServletRequest httpRequest) {
        oAuthAuthenticationService.execute(provider, request.code(), request.redirectUri(), httpRequest);
        return ResponseEntity.ok().build();
    }
}