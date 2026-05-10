package team.themoment.readygsmserver.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.readygsmserver.domain.user.entity.UserJpaEntity;
import team.themoment.readygsmserver.domain.user.entity.constant.Role;
import team.themoment.readygsmserver.domain.user.repository.UserRepository;
import team.themoment.sdk.exception.ExpectedException;

@Service
@RequiredArgsConstructor
@Profile("!prod")
public class ModifyUserRoleService {

    private final UserRepository userRepository;

    @Transactional
    public void execute(String email, Role role) {
        UserJpaEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExpectedException("해당 이메일에 해당하는 계정이 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        user.modifyRole(role);
    }
}
