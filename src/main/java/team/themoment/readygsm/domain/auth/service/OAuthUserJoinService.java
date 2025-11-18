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
public class OAuthUserJoinService { // 클래스명 변경

    private final UserJpaRepository userJpaRepository;

    /**
     * OAuth 정보를 기반으로 사용자를 찾고, 없으면 새로 생성하여 User DTO를 반환합니다.
     */
    public User findOrCreateUser(OAuthUserInfo userInfo) {

        // 이메일을 기준으로 사용자 조회
        UserJpaEntity userEntity = userJpaRepository.findByEmail(userInfo.getEmail());

        if (userEntity == null) {
            // 새 사용자 생성 및 저장
            log.debug("[OAuthUserJoinService] Creating new account for {}", userInfo.getEmail()); // 로그 메시지 수정
            // userInfo.toEntity()는 OAuthUserInfo에 정의되어 있다고 가정
            userEntity = userJpaRepository.save(userInfo.toEntity());
        }

        // Entity를 DTO (User)로 변환하여 반환 (UserJpaEntity에 toDto() 메서드가 정의되어 있다고 가정)
        return userEntity.toDto();
    }
}
