package team.themoment.readygsm.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.auth.dto.AuthResponse;
import team.themoment.readygsm.domain.auth.provider.OAuthProvider;
import team.themoment.readygsm.domain.user.data.User;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;
import team.themoment.readygsm.domain.user.repository.UserJpaRepository;
import team.themoment.readygsm.global.security.jwt.JwtProvider;
import team.themoment.readygsm.global.security.oauth.OAuthUserInfo;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignInService {

    private final OAuthProvider oAuthProvider;
    private final JwtProvider jwtProvider;
    private final UserJpaRepository userJpaRepository;

    public AuthResponse authenticateWithCode(String code) {
        log.info("[SignInService] Received raw code: {}", code);

        String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);
        log.info("[SignInService] Decoded code: {}", decodedCode);

        OAuthUserInfo userInfo = oAuthProvider.getUserInfo(decodedCode);

        if (userInfo == null) {
            log.error("[SignInService] userInfo is NULL from OAuthProvider");
            throw new IllegalStateException("OAuth user info could not be retrieved");
        }


        log.info("[OAuthUserInfo.email]: {}", userInfo.getEmail());

        UserJpaEntity userEntity = userJpaRepository.findByEmail(userInfo.getEmail());
        if (userEntity == null) {
            log.info("[SignInService] New user detected. Creating new account for {}", userInfo.getEmail());
            userEntity = userJpaRepository.save(userInfo.toEntity());
        }

        User user = userEntity.toDto();
        Long userId = user.getId();

        log.info("[Email]: {}", user.getEmail());
        log.info("[UserId]: {}", userId);

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









