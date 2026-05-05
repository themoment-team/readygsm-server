package team.themoment.readygsmserver.global.security.oauth2.feign;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import team.themoment.readygsmserver.global.security.oauth2.feign.dto.KakaoUserInfoResponse;

public interface KakaoUserInfoClient {

    @RequestLine("GET /v2/user/me")
    @Headers("Authorization: Bearer {accessToken}")
    KakaoUserInfoResponse getUserInfo(@Param("accessToken") String accessToken);
}