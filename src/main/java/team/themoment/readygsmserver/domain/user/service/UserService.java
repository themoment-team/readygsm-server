package team.themoment.readygsmserver.domain.user.service;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import team.themoment.readygsmserver.domain.user.dto.response.UserResDto;

@Service
public class UserService {

    public UserResDto getMe(OAuth2User user) {
        Long id = ((Number) user.getAttribute("id")).longValue();
        String email = user.getAttribute("email");
        String role = user.getAttribute("role");
        return new UserResDto(id, email, role);
    }
}