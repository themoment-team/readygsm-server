package team.themoment.readygsm.domain.reservation.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team.themoment.readygsm.domain.reservation.presentation.data.request.PostReservationReqDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostReservationResDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostUserReservationResDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.SearchReservationResDto;
import team.themoment.readygsm.domain.reservation.service.DeleteReservationService;
import team.themoment.readygsm.domain.reservation.service.PostReservationService;
import team.themoment.readygsm.domain.reservation.service.PostUserReservationService;
import team.themoment.readygsm.domain.reservation.service.SearchReservationService;
import team.themoment.readygsm.global.response.CommonApiResponse;

import java.util.List;

@Tag(name = "Reservation", description = "예약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {

    private final SearchReservationService searchReservationService;
    private final PostReservationService postReservationService;
    private final PostUserReservationService postUserReservationService;
    private final DeleteReservationService deleteReservationService;

    @Operation(summary = "예약 검색", description = "예약 관련 API")
    @GetMapping("/search")
    public List<SearchReservationResDto> searchReservation(
            @RequestParam(required = false) String activityName,
            @RequestParam(required = false) String applicantName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(defaultValue = "0",required = false) int page,
            @RequestParam(defaultValue = "10",required = false) int limit
    ) {
        return searchReservationService.searchReservation(
                activityName,
                applicantName,
                phoneNumber,
                page,
                limit);
    }

    @Operation(summary = "예약 추가", description = "로그인된 사용자의 예약을 생성합니다.")
    @PostMapping("/{activityId}")
    public PostReservationResDto postReservation(
            @RequestBody @Valid PostReservationReqDto reqDto,
            @PathVariable("activityId") Long activityId) {
        return postReservationService.PostReservation(activityId, reqDto);
    }

    @Operation(summary = "특정 사용자 예약 추가", description = "지정된 사용자 ID를 이용해 사용자의 예약을 생성합니다.")
    @PostMapping("/{activityId}/{userId}")
    public PostUserReservationResDto postUserReservation(
            @RequestBody @Valid PostReservationReqDto reqDto,
            @PathVariable("activityId") Long activityId,
            @PathVariable("userId") Long userId) {
        return postUserReservationService.postUserReservation(
                        reqDto, activityId, userId
        );
    }

    @Operation(summary = "예약 취소", description = "로그인된 사용자의 예약을 취소합니다.")
    @DeleteMapping("/{reservationId}")
    public CommonApiResponse<Void> deleteReservation(
            @PathVariable("reservationId") Long reservationId) {
        deleteReservationService.deleteReservation(reservationId);
        return CommonApiResponse.success("예약이 취소되었습니다.");
    }
}