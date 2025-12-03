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

    private final OAuthUserJoinService oAuthUserJoinService;


    public AuthResponse authenticateWithCode(String code) {

        log.debug("[SignInService] Received raw code: {}", code);
        String decodedCode = "";
        try {
            decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("[SignInService] Code decoding failed: {}", e.getMessage());
            throw new IllegalStateException("Authorization code decoding failed", e);
        }
        log.debug("[SignInService] Decoded code: {}", decodedCode);

        OAuthUserInfo userInfo = oAuthProvider.getUserInfo(decodedCode);

        if (userInfo == null) {
            log.error("[SignInService] userInfo is NULL from OAuthProvider");
            throw new IllegalStateException("OAuth user info could not be retrieved");
        }

        User user = oAuthUserJoinService.findOrCreateUser(userInfo);
        Long userId = user.getId();

        log.debug("[Email]: {}", user.getEmail());
        log.debug("[UserId]: {}", userId);

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

