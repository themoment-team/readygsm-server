package team.themoment.readygsmserver.global.security.oauth2.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.themoment.readygsmserver.domain.auth.dto.UserAuthInfo;
import team.themoment.readygsmserver.domain.user.entity.constant.AuthReferrerType;
import team.themoment.readygsmserver.global.config.KakaoOAuthProperties;
import team.themoment.readygsmserver.global.security.oauth2.feign.KakaoTokenClient;
import team.themoment.readygsmserver.global.security.oauth2.feign.KakaoUserInfoClient;
import team.themoment.readygsmserver.global.security.oauth2.feign.dto.KakaoTokenResponse;
import team.themoment.readygsmserver.global.security.oauth2.feign.dto.KakaoUserInfoResponse;

@Component
@RequiredArgsConstructor
public class KakaoOAuthProvider implements OAuthProvider {

    private static final String PROVIDER_NAME = "kakao";

    private final KakaoTokenClient kakaoTokenClient;
    private final KakaoUserInfoClient kakaoUserInfoClient;
    private final KakaoOAuthProperties kakaoOAuthProperties;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public UserAuthInfo getUserAuthInfo(String code, String redirectUri) {
        KakaoTokenResponse tokenResponse = kakaoTokenClient.getToken(
                code,
                kakaoOAuthProperties.clientId(),
                kakaoOAuthProperties.clientSecret(),
                redirectUri
        );

        KakaoUserInfoResponse userInfoResponse = kakaoUserInfoClient.getUserInfo(tokenResponse.accessToken());

        return new UserAuthInfo(userInfoResponse.email(), PROVIDER_NAME, AuthReferrerType.KAKAO);
    }
}