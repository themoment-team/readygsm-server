package team.themoment.readygsm.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.auth.dto.AuthResponse;
import team.themoment.readygsm.domain.auth.provider.OAuthProvider;
import team.themoment.readygsm.domain.user.data.User;
import team.themoment.readygsm.global.security.jwt.JwtProvider;
import team.themoment.readygsm.global.security.oauth.OAuthUserInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignInService {

    private final OAuthProvider oAuthProvider;
    private final JwtProvider jwtProvider;
    // 사용자 관리 책임을 OAuthUserJoinService로 변경/분리
    private final OAuthUserJoinService oAuthUserJoinService;

    // 주석: UserJpaRepository는 OAuthUserJoinService 내부에서 사용됨

    public AuthResponse authenticateWithCode(String code) {

        // 1. 인가 코드 디코딩 로직 유지 및 로깅 레벨 조정
        log.debug("[SignInService] Received raw code: {}", code);
        String decodedCode = "";
        try {
            decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("[SignInService] Code decoding failed: {}", e.getMessage());
            throw new IllegalStateException("Authorization code decoding failed", e);
        }
        log.debug("[SignInService] Decoded code: {}", decodedCode);

        // 2. OAuth 사용자 정보 획득
        OAuthUserInfo userInfo = oAuthProvider.getUserInfo(decodedCode);

        if (userInfo == null) {
            log.error("[SignInService] userInfo is NULL from OAuthProvider");
            throw new IllegalStateException("OAuth user info could not be retrieved");
        }

        // 3. 사용자 조회 또는 생성 책임을 OAuthUserJoinService에 위임
        // 필드명 변경에 맞춰 메서드 호출
        User user = oAuthUserJoinService.findOrCreateUser(userInfo);
        Long userId = user.getId();

        log.debug("[Email]: {}", user.getEmail());
        log.debug("[UserId]: {}", userId);

        // 4. JWT 토큰 생성 및 응답 빌드
        String token = jwtProvider.createToken(userId, user.getRole());

        long issuedAtEpoch = System.currentTimeMillis();
        long expiresAtEpoch = issuedAtEpoch + (jwtProvider.getExpirationMinutes() * 60 * 1000L);

        return AuthResponse.builder()
                .userId(userId)
                .token(token)
                .tokenIssuedAt(issuedAtEpoch)
                .tokenExpiresAt(expiresAtEpoch)
                .role(user.getRole())
                .build();
    }
}

