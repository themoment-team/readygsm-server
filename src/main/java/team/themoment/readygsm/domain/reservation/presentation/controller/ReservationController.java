package team.themoment.readygsm.domain.reservation.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.themoment.readygsm.domain.reservation.presentation.data.request.PostReservationReqDto;
import team.themoment.readygsm.domain.reservation.presentation.data.response.PostReservationResDto;
import team.themoment.readygsm.domain.reservation.service.ReservationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{activityId}")
    public ResponseEntity<PostReservationResDto> postReservation(
            @RequestBody PostReservationReqDto reqDto,
            @PathVariable("activityId") String activityId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                reservationService.PostReservation(1L, Long.valueOf(activityId), reqDto));
    }
}
