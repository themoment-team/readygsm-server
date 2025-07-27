package team.themoment.readygsm.global.security.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity; // ✅ User → UserJpaEntity
import team.themoment.readygsm.domain.user.repository.UserJpaRepository;
import team.themoment.readygsm.global.security.jwt.JwtProvider;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserJpaRepository userJpaRepository;
    private final JwtProvider jwtProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2 기본 사용자 정보 로드
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 어떤 OAuth 공급자인지 구분 (ex. google, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 정보 파싱 (email, name 등 추출)
        OAuthAttributes authAttributes = OAuthAttributes.of(registrationId, oAuth2User.getAttributes());

        // 사용자 저장 또는 갱신
        UserJpaEntity user = saveOrUpdate(authAttributes);

        // JWT 토큰 발급
        String jwt = jwtProvider.createToken(user.getId(), user.getRole());

        // 커스텀 OAuth2User 객체로 반환
        return new CustomOAuth2User(user, oAuth2User.getAttributes(), jwt); // ✅ authAttributes.getAttributes() → oAuth2User.getAttributes()
    }

    // 사용자 저장 또는 존재하면 반환
    private UserJpaEntity saveOrUpdate(OAuthAttributes attributes) {
        return userJpaRepository.findByEmail(attributes.getEmail()) // ✅ 반드시 이 메서드가 UserJpaRepository에 정의되어 있어야 함
                .orElseGet(() -> userJpaRepository.save(attributes.toEntity()));
    }
}
