package team.themoment.readygsm.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    CLIENT_REGISTRATION_NOT_FOUND("요청된 OAuth 제공자의 ClientRegistration 정보를 찾을 수 없습니다.",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
    );

    private final String message;
    private final int status;
}