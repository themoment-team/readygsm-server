package team.themoment.readygsm.domain.activity.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team.themoment.readygsm.domain.activity.presentation.data.request.PatchActivityReqDto;
import team.themoment.readygsm.domain.activity.presentation.data.request.PostActivityReqDto;
import team.themoment.readygsm.domain.activity.presentation.data.response.PatchActivityResDto;
import team.themoment.readygsm.domain.activity.presentation.data.response.PostActivityResDto;
import team.themoment.readygsm.domain.activity.presentation.data.response.SearchActivityResDto;
import team.themoment.readygsm.domain.activity.presentation.data.response.ViewActivityResDto;
import team.themoment.readygsm.domain.activity.service.*;
import team.themoment.readygsm.global.response.CommonApiResponse;

import java.util.List;

@Tag(name = "Activity", description = "활동 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activity")
public class ActivityController {

    private final ViewActivityService viewActivityService;
    private final SearchActivityService searchActivityService;
    private final PostActivityService postActivityService;
    private final PatchActivityService patchActivityService;
    private final DeleteActivityService deleteActivityService;

    @Operation(summary = "활동 조회", description = "활동 id로 활동을 조회합니다.")
    @GetMapping("/{activityId}")
    public ViewActivityResDto viewActivity(
            @PathVariable("activityId") Long activityId
    ) {
        return viewActivityService.viewActivity(activityId);
    }

    @Operation(summary = "활동 검색", description = "활동 이름으로 활동을 검색합니다.")
    @GetMapping("/search")
    public List<SearchActivityResDto> searchActivity(
            @RequestParam String name,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int limit
    ) {
        return searchActivityService.searchActivity(name, page, limit);
    }

    @Operation(summary = "활동 추가", description = "활동을 추가합니다.")
    @PostMapping("/")
    public PostActivityResDto postActivity(
            @RequestBody @Valid PostActivityReqDto postActivityReqDto
    ) {
        return postActivityService.postActivity(postActivityReqDto);
    }

    @Operation(summary = "활동 수정", description = "활동을 수정합니다.")
    @PatchMapping("/{activityId}")
    public PatchActivityResDto editActivity(
            @RequestBody @Valid PatchActivityReqDto patchActivityReqDto,
            @PathVariable Long activityId
    ) {
        return patchActivityService.editActivity(patchActivityReqDto, activityId);
    }

    @Operation(summary = "활동 삭제", description = "활동 id로 활동을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 활동 삭제에 성공함"),
            @ApiResponse(responseCode = "403", description = "접근권한 조건을 충족하는데 실패함", content = @Content()),
            @ApiResponse(responseCode = "404", description = "해당 id를 가진 활동이 없음", content = @Content())
    })
    @DeleteMapping("/{activityId}")
    public CommonApiResponse deleteActivity(
            @PathVariable Long activityId
    ) {
        deleteActivityService.deleteActivity(activityId);
        return CommonApiResponse.success("활동이 삭제되었습니다.");
    }
}
