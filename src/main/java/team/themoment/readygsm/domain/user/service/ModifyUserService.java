package team.themoment.readygsm.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.themoment.readygsm.domain.user.data.User;
import team.themoment.readygsm.domain.user.exception.UserNotFoundException;
import team.themoment.readygsm.domain.user.presentation.data.response.PatchUserResDto;
import team.themoment.readygsm.domain.user.repository.UserJpaRepository;

@Service
@RequiredArgsConstructor
public class ModifyUserService {

    private final UserJpaRepository userJpaRepository;

    public PatchUserResDto execute(Long userId, String name, String email) {
        // TODO:사용자 Role이 ADMIN이 아니라면 다른 사용자의 정보를 수정할 수 없도록 추가 구현 필요
        User user = userJpaRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new)
                .toDto();

        userJpaRepository.save(User.builder()
                .id(user.getId())
                .name(name != null ? name : user.getName())
                .email(email != null ? email : user.getEmail())
                .role(user.getRole())
                .build()
                .toEntity()
        );
        return new PatchUserResDto(user.getName(), user.getEmail());
    }
}