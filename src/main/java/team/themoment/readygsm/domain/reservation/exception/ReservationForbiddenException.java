package team.themoment.readygsm.domain.reservation.exception;

import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

public class ReservationForbiddenException extends ExpectedException {
    public ReservationForbiddenException() {
        super(ErrorCode.RESERVATION_FORBIDDEN);
    }
}