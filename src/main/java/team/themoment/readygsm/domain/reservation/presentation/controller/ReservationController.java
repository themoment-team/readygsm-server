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

    @PostMapping("/{activityId}")
    public ResponseEntity<PostReservationResDto> postReservation(
            @RequestBody @Valid PostReservationReqDto reqDto,
            @PathVariable("activityId") Long activityId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                postReservationService.PostReservation(activityId, reqDto));
    }

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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reservationId}")
    public void deleteReservation(
            @PathVariable("reservationId") Long reservationId) {
        deleteReservationService.deleteReservation(reservationId);
    }
}
