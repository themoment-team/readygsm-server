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
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.auth.dto.UserAuthInfo;
import team.themoment.readygsmserver.domain.user.entity.UserJpaEntity;
import team.themoment.readygsmserver.domain.user.repository.UserRepository;
import team.themoment.readygsmserver.global.security.oauth2.OAuthProviderFactory;
import team.themoment.readygsmserver.global.security.oauth2.provider.OAuthProvider;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthAuthenticationService {

    static final String OAUTH2_STATE_SESSION_KEY = "oauth2_state";
    private static final int SESSION_TIMEOUT_SECONDS = 3 * 60 * 60;

    private final OAuthProviderFactory oAuthProviderFactory;
    private final UserRepository userRepository;

    public void execute(String providerName, String code, String state, HttpServletRequest request) {
        validateState(state, request);

        OAuthProvider provider = oAuthProviderFactory.getProvider(providerName);
        UserAuthInfo userAuthInfo = provider.getUserAuthInfo(code);

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

        // 세션 고정 공격 방지: 기존 세션 무효화 후 새 세션 발급
        request.getSession(false).invalidate();
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        newSession.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
    }

    private void validateState(String state, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalStateException("OAuth2 인증 세션이 존재하지 않습니다.");
        }
        String savedState = (String) session.getAttribute(OAUTH2_STATE_SESSION_KEY);
        if (savedState == null || !savedState.equals(state)) {
            throw new IllegalArgumentException("유효하지 않은 state 값입니다. CSRF 공격이 의심됩니다.");
        }
        session.removeAttribute(OAUTH2_STATE_SESSION_KEY);
    }
}