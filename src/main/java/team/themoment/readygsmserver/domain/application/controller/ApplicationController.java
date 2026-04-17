package team.themoment.readygsmserver.domain.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.readygsmserver.domain.application.dto.request.ApplicationReqDto;
import team.themoment.readygsmserver.domain.application.dto.response.ApplicationResDto;
import team.themoment.readygsmserver.domain.user.dto.response.UserResDto;

import java.util.List;

@RestController
@Tag(name = "Application", description = "활동 신청 API")
@RequestMapping("/api/v1/application")
public class ApplicationController {

    @Operation(summary = "활동 신청", description = "활동을 신청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "활동을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 신청한 활동")
    })
    @PostMapping("/apply")
    public ApplicationResDto applyActivity(@RequestParam Long activityId, @RequestBody ApplicationReqDto req) {
        return null;
    }

    @Operation(summary = "활동 신청 취소", description = "신청된 활동을 신청 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "신청 내역을 찾을 수 없음")
    })
    @DeleteMapping("/cancel")
    public void cancelApplication(@RequestParam Long activityId) {
    }

    @Operation(summary = "신청한 학생 조회", description = "활동을 신청한 학생 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/admin/applications")
    public List<UserResDto> queryStudents(@RequestParam Long activityId) {
        return null;
    }

    @Operation(summary = "특정 학생 활동 신청 취소", description = "id에 해당하는 학생의 활동 신청을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "신청 내역을 찾을 수 없음")
    })
    @DeleteMapping("/admin/cancel/{id}")
    public void deleteApplication(@PathVariable Long id) {
    }

    @Operation(summary = "활동 신청자 엑셀 출력", description = "활동을 신청한 학생 목록을 엑셀로 출력합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출력 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/admin/excel")
    public MultipartFile queryApplication(@RequestParam Long activityId) {
        return null;
    }
}
