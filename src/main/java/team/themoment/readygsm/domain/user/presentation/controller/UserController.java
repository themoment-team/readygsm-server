package team.themoment.readygsm.domain.user.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.presentation.data.response.GetUserResDto;
import team.themoment.readygsm.domain.user.presentation.data.response.PatchUserResDto;
import team.themoment.readygsm.domain.user.service.FindUserService;
import team.themoment.readygsm.domain.user.service.ModifyUserService;
import team.themoment.readygsm.domain.user.service.SearchUserService;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "User", description = "사용자 정보 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final FindUserService findUserService;
    private final SearchUserService searchUserService;
    private final ModifyUserService modifyUserService;

    @Operation(summary = "사용자 정보 조회", description = "사용자의 ID로 사용자 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResDto> getUser(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(findUserService.execute(userId));
    }

    @Operation(summary = "현재 사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<GetUserResDto> getCurrentUser() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new GetUserResDto(
                1L, "Test User", "s00000@gsm.hs.kr", UserRole.USER, new ArrayList<>()));
    }

    @Operation(summary = "사용자 검색", description = "이름, 이메일, 역할로 사용자를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<GetUserResDto>> searchUsers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "role", required = false) UserRole role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "255") int limit
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(searchUserService.execute(name, email, role, page, limit));
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자의 ID로 사용자 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
            @ApiResponse(responseCode = "403", description = "사용자 권한이 부족함", content = @Content()),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content())
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<PatchUserResDto> updateUser(
            @PathVariable(value = "userId") Long userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(modifyUserService.execute(userId, name, email));
    }
}