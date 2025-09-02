package team.themoment.readygsm.domain.reservation.exception;

import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

public class ReservationNonUniqueException extends ExpectedException {
    public ReservationNonUniqueException() {
        super(ErrorCode.RESERVATION_NON_UNIQUE);
    }
}