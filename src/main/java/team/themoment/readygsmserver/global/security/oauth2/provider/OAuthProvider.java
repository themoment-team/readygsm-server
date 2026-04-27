package team.themoment.readygsmserver.global.security.oauth2.provider;

import team.themoment.readygsmserver.domain.auth.dto.UserAuthInfo;

public interface OAuthProvider {
    String getProviderName();
    UserAuthInfo getUserAuthInfo(String code);
}
