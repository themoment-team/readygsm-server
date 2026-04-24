package team.themoment.readygsmserver.global.security.oauth2.feign;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import team.themoment.readygsmserver.global.security.oauth2.feign.dto.GoogleUserInfoResponse;

public interface GoogleUserInfoClient {

    @RequestLine("GET /oauth2/v2/userinfo")
    @Headers("Authorization: Bearer {accessToken}")
    GoogleUserInfoResponse getUserInfo(@Param("accessToken") String accessToken);
}