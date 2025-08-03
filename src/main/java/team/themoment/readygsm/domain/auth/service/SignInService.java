package team.themoment.readygsm.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.auth.dto.AuthResponse;
import team.themoment.readygsm.domain.auth.entity.AuthJpaEntity;
import team.themoment.readygsm.domain.auth.provider.OAuthProvider;
import team.themoment.readygsm.domain.auth.repository.AuthJpaRepository;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.global.security.jwt.JwtProvider;
import team.themoment.readygsm.global.security.oauth.OAuthUserInfo;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final OAuthProvider oAuthProvider;
    private final JwtProvider jwtProvider;
    private final AuthJpaRepository authJpaRepository; // 사용자의 회원가입 여부 확인

    public AuthResponse authenticateWithCode(String code) {
        OAuthUserInfo userInfo = oAuthProvider.getUserInfo(code);

        // DB에서 유저 조회 또는 신규 생성
        AuthJpaEntity user = authJpaRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> authJpaRepository.save(userInfo.toEntity()));

        String token = jwtProvider.createToken(user.getId(), user.getRole());
        LocalDateTime now = LocalDateTime.now();

        return AuthResponse.builder()
                .userId(user.getId())
                .token(token)
                .tokenIssuedAt(now)
                .tokenExpiresAt(now.plusMinutes(jwtProvider.getExpirationMinutes()))
                .role(UserRole.USER)
                .build();
    }
}
