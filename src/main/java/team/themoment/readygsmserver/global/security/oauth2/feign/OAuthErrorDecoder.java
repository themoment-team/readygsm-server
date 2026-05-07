package team.themoment.readygsmserver.global.security.oauth2.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import team.themoment.sdk.exception.ExpectedException;

public class OAuthErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        if (status.is4xxClientError()) {
            return new ExpectedException("유효하지 않거나 만료된 인가 코드입니다.", HttpStatus.BAD_REQUEST);
        }

        return new ExpectedException("OAuth 서버와 통신 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
