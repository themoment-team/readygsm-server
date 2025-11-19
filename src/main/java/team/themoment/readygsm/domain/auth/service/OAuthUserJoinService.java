package team.themoment.readygsm.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsm.domain.user.data.User;
import team.themoment.readygsm.domain.user.entity.UserJpaEntity;
import team.themoment.readygsm.domain.user.repository.UserJpaRepository;
import team.themoment.readygsm.global.security.oauth.OAuthUserInfo;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OAuthUserJoinService {

    private final UserJpaRepository userJpaRepository;

    public User findOrCreateUser(OAuthUserInfo userInfo) {

        UserJpaEntity userEntity = userJpaRepository.findByEmail(userInfo.getEmail());

        if (userEntity == null) {
            log.debug("[OAuthUserJoinService] Creating new account for {}", userInfo.getEmail());
            userEntity = userJpaRepository.save(userInfo.toEntity());
        }

        return userEntity.toDto();
    }
}
