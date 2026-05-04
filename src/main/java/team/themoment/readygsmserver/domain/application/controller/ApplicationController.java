package team.themoment.readygsmserver.domain.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import team.themoment.readygsmserver.domain.application.dto.request.ApplicationReqDto;
import team.themoment.readygsmserver.domain.application.dto.response.ApplicationResDto;
import team.themoment.readygsmserver.domain.application.service.ApplyActivityService;
import team.themoment.readygsmserver.domain.application.service.CancelApplicationService;
import team.themoment.readygsmserver.domain.application.service.DeleteApplicationService;
import team.themoment.readygsmserver.domain.application.service.ExportApplicationExcelService;
import team.themoment.readygsmserver.domain.application.service.QueryApplicationsService;
import team.themoment.readygsmserver.global.security.annotation.AuthRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@Tag(name = "Application", description = "활동 신청 API")
@RequestMapping("/api/v1/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplyActivityService applyActivityService;
    private final CancelApplicationService cancelApplicationService;
    private final QueryApplicationsService queryApplicationsService;
    private final DeleteApplicationService deleteApplicationService;
    private final ExportApplicationExcelService exportApplicationExcelService;

    @Operation(summary = "활동 신청", description = "활동을 신청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "활동을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "이미 신청한 활동")
    })
    @PostMapping("/apply")
    public ApplicationResDto applyActivity(@AuthRequest Long userId, @RequestParam Long activityId, @Valid @RequestBody ApplicationReqDto req) {
        return applyActivityService.execute(userId, activityId, req);
    }

    @Operation(summary = "활동 신청 취소", description = "신청된 활동을 신청 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "신청 내역을 찾을 수 없음")
    })
    @DeleteMapping("/cancel")
    public void cancelApplication(@AuthRequest Long userId, @RequestParam Long activityId) {
        cancelApplicationService.execute(userId, activityId);
    }

    @Operation(summary = "신청한 학생 조회", description = "활동을 신청한 학생 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/admin/applications")
    public List<ApplicationResDto> queryStudents(@RequestParam Long activityId) {
        return queryApplicationsService.execute(activityId);
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
        deleteApplicationService.execute(id);
    }

    @Operation(summary = "활동 신청자 엑셀 출력", description = "활동을 신청한 학생 목록을 엑셀로 출력합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출력 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/admin/excel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam Long activityId) {
        byte[] excelBytes = exportApplicationExcelService.execute(activityId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("applications.xlsx", StandardCharsets.UTF_8).build()
        );

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
}
