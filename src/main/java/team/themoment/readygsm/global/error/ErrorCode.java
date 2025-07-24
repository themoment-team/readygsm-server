package team.themoment.readygsm.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* User */
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),

    /* Activity */
    ACTIVITY_NOT_FOUND("활동을 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),

    /* Reservation */
    RESERVATION_NOT_FOUND("예약을 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());

    private final String message;
    private final int status;
}