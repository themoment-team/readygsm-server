package team.themoment.readygsmserver.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Auth", description = "인증 API")
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Operation(summary = "구글 로그인", description = "Google OAuth2 로그인 페이지로 리다이렉트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Google 로그인 페이지로 리다이렉트")
    })
    @GetMapping("/login")
    public void login() {
    }

    @Operation(summary = "구글 로그인 콜백", description = "Google OAuth2 인가 코드를 처리하고 JWT를 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 및 JWT 발급"),
            @ApiResponse(responseCode = "400", description = "잘못된 인가 코드"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/callback")
    public void callback() {
    }
}
