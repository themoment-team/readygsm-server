package team.themoment.readygsmserver.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.auth.dto.UserAuthInfo;
import team.themoment.readygsmserver.domain.user.entity.UserJpaEntity;
import team.themoment.readygsmserver.domain.user.repository.UserRepository;
import team.themoment.readygsmserver.global.config.GoogleOAuthProperties;
import team.themoment.readygsmserver.global.security.oauth2.OAuth2Properties;
import team.themoment.readygsmserver.global.security.oauth2.OAuthProviderFactory;
import team.themoment.readygsmserver.global.security.oauth2.provider.OAuthProvider;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthAuthenticationService {

    private static final String OAUTH2_STATE_REDIS_PREFIX = "oauth2:state:";
    private static final Duration OAUTH2_STATE_TTL = Duration.ofMinutes(5);

    private final OAuthProviderFactory oAuthProviderFactory;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final OAuth2Properties oauth2Properties;
    private final GoogleOAuthProperties googleOAuthProperties;

    public String generateState(String redirectUri) {
        String resolvedRedirectUri = resolveRedirectUri(redirectUri);
        String state = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(OAUTH2_STATE_REDIS_PREFIX + state, resolvedRedirectUri, OAUTH2_STATE_TTL);
        return state;
    }

    public void execute(String providerName, String code, String state, HttpServletRequest request) {
        String redirectUri = validateState(state);

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

    private String validateState(String state) {
        String key = OAUTH2_STATE_REDIS_PREFIX + state;
        String redirectUri = redisTemplate.opsForValue().getAndDelete(key);
        if (redirectUri == null) {
            throw new IllegalArgumentException("유효하지 않은 state 값입니다. CSRF 공격이 의심됩니다.");
        }
        return redirectUri;
    }

    private String resolveRedirectUri(String redirectUri) {
        if (redirectUri == null || redirectUri.isBlank()) {
            return googleOAuthProperties.redirectUri();
        }
        if (!oauth2Properties.allowedRedirectUris().contains(redirectUri)) {
            throw new IllegalArgumentException("허용되지 않은 redirect_uri입니다: " + redirectUri);
        }
        return redirectUri;
    }
}
