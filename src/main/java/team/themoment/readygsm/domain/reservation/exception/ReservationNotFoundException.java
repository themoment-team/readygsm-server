package team.themoment.readygsm.domain.reservation.exception;

import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

public class ReservationNotFoundException extends ExpectedException {
    public ReservationNotFoundException() {
        super(ErrorCode.RESERVATION_NOT_FOUND);
    }
}