package team.themoment.readygsm.global.error.exception;

import lombok.Getter;
import team.themoment.readygsm.global.error.ErrorCode;

@Getter
public class ExpectedException extends RuntimeException {
    private final ErrorCode errorCode;

    public ExpectedException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}