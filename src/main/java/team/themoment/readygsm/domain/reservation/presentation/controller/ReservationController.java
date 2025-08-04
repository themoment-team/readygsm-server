package team.themoment.readygsm.domain.reservation.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.themoment.readygsm.domain.reservation.presentation.data.request.PostReservationReqDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostReservationResDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostUserReservationResDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.SearchReservationResDto;
import team.themoment.readygsm.domain.reservation.service.DeleteReservationService;
import team.themoment.readygsm.domain.reservation.service.PostReservationService;
import team.themoment.readygsm.domain.reservation.service.PostUserReservationService;
import team.themoment.readygsm.domain.reservation.service.SearchReservationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {

    private final SearchReservationService searchReservationService;
    private final PostReservationService postReservationService;
    private final PostUserReservationService postUserReservationService;
    private final DeleteReservationService deleteReservationService;

    /* 예약 검색 */
    /* 접근 권한 : TEACHER */
    @GetMapping("/search")
    public ResponseEntity<List<SearchReservationResDto>> searchReservation(
            @RequestParam(required = false) String activityName,
            @RequestParam(required = false) String applicantName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(defaultValue = "0",required = false) int page,
            @RequestParam(defaultValue = "10",required = false) int limit
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                searchReservationService.searchReservation(
                activityName,
                applicantName,
                phoneNumber,
                page,
                limit));
    }

    /* 활동 예약 */
    /* 접근 권한 : STUDENT */
    @PostMapping("/{activityId}")
    public ResponseEntity<PostReservationResDto> postReservation(
            @RequestBody @Valid PostReservationReqDto reqDto,
            @PathVariable("activityId") Long activityId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                // 첫번째 인자는 header에 jwt토큰에서 userId를 보내는것을 구현해주셔야 합니다
                postReservationService.PostReservation(1L, activityId, reqDto));
    }

    /* 특정 사용자 예약 추가 */
    /* 접근 권한 : TEACHER */
    @PostMapping("/{activityId}/{userId}")
    public ResponseEntity<PostUserReservationResDto> postUserReservation(
            @RequestBody @Valid PostReservationReqDto reqDto,
            @PathVariable("activityId") Long activityId,
            @PathVariable("userId") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                postUserReservationService.postUserReservation(
                        reqDto, activityId, userId
                )
        );
    }

    /* 예약 취소 */
    /* 접근 권한 : STUDENT */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reservationId}")
    public void deleteReservation(
            @PathVariable("reservationId") Long reservationId) {
        // 첫번째 인자는 header에 jwt토큰에서 userId를 보내는것을 구현해주셔야 합니다
        deleteReservationService.deleteReservation(1L, reservationId);
    }
}
