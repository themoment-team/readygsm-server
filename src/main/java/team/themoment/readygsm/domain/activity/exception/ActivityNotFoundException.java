package team.themoment.readygsm.domain.activity.exception;

import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

public class ActivityNotFoundException extends ExpectedException {
    public ActivityNotFoundException() {
        super(ErrorCode.ACTIVITY_NOT_FOUND);
    }
}