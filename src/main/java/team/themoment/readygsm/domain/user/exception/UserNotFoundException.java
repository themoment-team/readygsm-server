package team.themoment.readygsm.domain.user.exception;

import team.themoment.readygsm.global.error.ErrorCode;
import team.themoment.readygsm.global.error.exception.ExpectedException;

public class UserNotFoundException extends ExpectedException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}