package team.themoment.readygsmserver.domain.activity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import team.themoment.readygsmserver.domain.activity.dto.request.ActivityReqDto;
import team.themoment.readygsmserver.domain.activity.dto.response.ActivityResDto;

import java.util.List;

@RestController
@Tag(name = "Activity", description = "활동 API")
@RequestMapping("/api/v1/activity")
public class ActivityController {

    @Operation(summary = "전체 활동 조회", description = "서비스에 등록되어 있는 모든 활동 목록과 각 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping
    public List<ActivityResDto> queryAllActivities() {
        return null;
    }

    @Operation(summary = "활동 단일 조회", description = "활동 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "활동을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ActivityResDto queryActivity(@PathVariable Long id) {
        return null;
    }

    @Operation(summary = "활동 생성", description = "활동을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PostMapping("/create")
    public ActivityResDto createActivity(@RequestBody ActivityReqDto req) {
        return null;
    }

    @Operation(summary = "활동 수정", description = "활동 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "활동을 찾을 수 없음")
    })
    @PatchMapping("/edit/{id}")
    public ActivityResDto editActivity(@PathVariable Long id, @RequestBody ActivityReqDto req) {
        return null;
    }

    @Operation(summary = "활동 삭제", description = "활동을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "활동을 찾을 수 없음")
    })
    @DeleteMapping("/delete/{id}")
    public void deleteActivity(@PathVariable Long id) {
    }
}
