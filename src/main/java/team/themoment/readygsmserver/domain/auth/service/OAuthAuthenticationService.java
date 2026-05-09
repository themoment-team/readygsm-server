package team.themoment.readygsmserver.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.auth.dto.UserAuthInfo;
import team.themoment.readygsmserver.domain.user.entity.UserJpaEntity;
import team.themoment.readygsmserver.domain.user.repository.UserRepository;
import team.themoment.readygsmserver.global.security.oauth2.OAuth2Properties;
import team.themoment.readygsmserver.global.security.oauth2.OAuthProviderFactory;
import team.themoment.readygsmserver.global.security.oauth2.provider.OAuthProvider;
import team.themoment.sdk.exception.ExpectedException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthAuthenticationService {

    private final OAuthProviderFactory oAuthProviderFactory;
    private final UserRepository userRepository;
    private final OAuth2Properties oauth2Properties;

    public void execute(String providerName, String code, String redirectUri, HttpServletRequest request) {
        validateRedirectUri(redirectUri);

        OAuthProvider provider = oAuthProviderFactory.getProvider(providerName);
        UserAuthInfo userAuthInfo = provider.getUserAuthInfo(code, redirectUri);

        UserJpaEntity user = userRepository
                .findByAuthReferrerTypeAndEmail(userAuthInfo.authReferrerType(), userAuthInfo.email())
                .orElseGet(() -> userRepository.save(
                        UserJpaEntity.buildMemberWithOauthInfo(userAuthInfo.email(), userAuthInfo.authReferrerType())
                ));

        user.updateLastLoginTime();

        OAuth2User oAuth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRole().getAuthority())),
                Map.of(
                        "id", user.getId(),
                        "role", user.getRole().name(),
                        "provider", userAuthInfo.provider(),
                        "email", user.getEmail(),
                        "last_login_time", user.getLastLoginTime().toString()
                ),
                "id"
        );

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                oAuth2User.getAuthorities(),
                userAuthInfo.provider()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    private void validateRedirectUri(String redirectUri) {
        if (redirectUri == null || redirectUri.isBlank()) {
            throw new ExpectedException("redirect_uri는 필수입니다.", HttpStatus.BAD_REQUEST);
        }
        if (!oauth2Properties.allowedRedirectUris().contains(redirectUri)) {
            throw new ExpectedException("허용되지 않은 redirect_uri입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}