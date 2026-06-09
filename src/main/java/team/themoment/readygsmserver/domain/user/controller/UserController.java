package team.themoment.readygsmserver.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.readygsmserver.domain.user.dto.response.UserResDto;
import team.themoment.readygsmserver.domain.user.service.QueryUserService;

@RestController
@Tag(name = "User", description = "유저 API")
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final QueryUserService queryUserService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 유저의 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/me")
    public UserResDto queryMe(@AuthenticationPrincipal OAuth2User user) {
        return queryUserService.execute(user);
    }
}