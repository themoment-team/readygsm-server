package team.themoment.readygsm.global.error.exception;

import lombok.Getter;
import team.themoment.readygsm.global.error.ErrorCode;

@Getter
public class ReadyGsmException extends RuntimeException {
    private final ErrorCode errorCode;

    public ReadyGsmException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}