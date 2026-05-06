package team.themoment.readygsmserver.global.security.oauth2.feign;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import team.themoment.readygsmserver.global.security.oauth2.feign.dto.GoogleTokenResponse;

public interface GoogleTokenClient {

    @RequestLine("POST /token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("code={code}&client_id={clientId}&client_secret={clientSecret}&redirect_uri={redirectUri}&grant_type=authorization_code")
    GoogleTokenResponse getToken(
            @Param("code") String code,
            @Param("clientId") String clientId,
            @Param("clientSecret") String clientSecret,
            @Param("redirectUri") String redirectUri
    );
}