package team.themoment.readygsmserver.domain.user.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import team.themoment.readygsmserver.domain.user.dto.response.UserResDto;

@Service
public class UserService {

    public UserResDto getMe() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken token) {
            OAuth2User user = token.getPrincipal();
            Long id = ((Number) user.getAttribute("id")).longValue();
            String email = user.getAttribute("email");
            String role = user.getAttribute("role");
            return new UserResDto(id, email, role);
        }

        throw new AccessDeniedException("인증 정보가 없습니다.");
    }
}
