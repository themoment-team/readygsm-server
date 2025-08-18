package team.themoment.readygsm.domain.activity.exception;

import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

public class ActivityFullException extends ExpectedException {
    public ActivityFullException() {
        super(ErrorCode.ACTIVITY_FULL);
    }
}