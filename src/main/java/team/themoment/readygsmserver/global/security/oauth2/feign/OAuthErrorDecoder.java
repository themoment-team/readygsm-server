package team.themoment.readygsmserver.global.security.oauth2.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import team.themoment.sdk.exception.ExpectedException;

public class OAuthErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.resolve(response.status());

        if (status != null && status.is4xxClientError()) {
            String message = methodKey.contains("TokenClient")
                    ? "유효하지 않거나 만료된 인가 코드입니다."
                    : "OAuth 인증 정보가 유효하지 않습니다.";
            return new ExpectedException(message, HttpStatus.BAD_REQUEST);
        }

        return new ExpectedException("OAuth 서버와 통신 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
