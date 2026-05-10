package team.themoment.readygsmserver.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import team.themoment.readygsmserver.domain.user.entity.constant.Role;
import team.themoment.readygsmserver.domain.user.service.ModifyUserRoleService;

@RestController
@Tag(name = "Utility", description = "개발용 유틸리티 API")
@RequestMapping("/api/v1/utility")
@RequiredArgsConstructor
@Profile("!prod")
public class UtilityController {

    private final ModifyUserRoleService modifyUserRoleService;

    @Operation(summary = "사용자 권한 수정", description = "입력된 이메일에 해당하는 사용자의 권한을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 권한 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 이메일에 해당하는 계정이 존재하지 않음")
    })
    @PatchMapping("/user/role")
    public String updateUserRole(@RequestParam String email, @RequestParam Role role) {
        modifyUserRoleService.execute(email, role);
        return "입력된 이메일에 해당하는 사용자의 권한을 수정했습니다. 이메일: " + email + ", 권한: " + role;
    }
}