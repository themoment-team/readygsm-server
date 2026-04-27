package team.themoment.readygsmserver.global.security.oauth2.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.themoment.readygsmserver.domain.auth.dto.UserAuthInfo;
import team.themoment.readygsmserver.domain.user.entity.constant.AuthReferrerType;
import team.themoment.readygsmserver.global.config.GoogleOAuthProperties;
import team.themoment.readygsmserver.global.security.oauth2.feign.GoogleTokenClient;
import team.themoment.readygsmserver.global.security.oauth2.feign.GoogleUserInfoClient;
import team.themoment.readygsmserver.global.security.oauth2.feign.dto.GoogleTokenResponse;
import team.themoment.readygsmserver.global.security.oauth2.feign.dto.GoogleUserInfoResponse;

@Component
@RequiredArgsConstructor
public class GoogleOAuthProvider implements OAuthProvider {

    private static final String PROVIDER_NAME = "google";

    private final GoogleTokenClient googleTokenClient;
    private final GoogleUserInfoClient googleUserInfoClient;
    private final GoogleOAuthProperties googleOAuthProperties;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public UserAuthInfo getUserAuthInfo(String code) {
        GoogleTokenResponse tokenResponse = googleTokenClient.getToken(
                code,
                googleOAuthProperties.clientId(),
                googleOAuthProperties.clientSecret(),
                googleOAuthProperties.redirectUri()
        );

        GoogleUserInfoResponse userInfoResponse = googleUserInfoClient.getUserInfo(tokenResponse.accessToken());

        return new UserAuthInfo(userInfoResponse.email(), PROVIDER_NAME, AuthReferrerType.GOOGLE);
    }
}
