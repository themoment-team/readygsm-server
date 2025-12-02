package team.themoment.readygsm.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* User */
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    CLIENT_REGISTRATION_NOT_FOUND("요청된 OAuth 제공자의 ClientRegistration 정보를 찾을 수 없습니다.",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
    ),

    /* Activity */
    ACTIVITY_FULL("활동 인원이 가득 찼습니다.", HttpStatus.CONFLICT.value()),
    ACTIVITY_NOT_FOUND("활동을 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),

    /* Reservation */
    RESERVATION_NOT_FOUND("예약을 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    RESERVATION_FORBIDDEN("본인의 예약이 아닙니다.", HttpStatus.FORBIDDEN.value()),
    RESERVATION_NON_UNIQUE("중복된 id의 예약이 존재합니다.", HttpStatus.CONFLICT.value());

    private final String message;
    private final int status;
}