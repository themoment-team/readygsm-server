package team.themoment.readygsm.domain.auth.provider;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import team.themoment.readygsm.global.security.oauth.OAuthUserInfo;

import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    public OAuthUserInfo getUserInfo(String code) {

        String accessToken = requestAccessToken(code);

        Map<String, Object> attributes = requestUserAttributes(accessToken);

        attributes.put("role", determineRole(attributes));

        return new OAuthUserInfo(attributes);
    }

    private String requestAccessToken(String code) {
           return "mock_access_token";
    }

    private Map<String, Object> requestUserAttributes(String accessToken) {
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("email", "test@example.com");
        attributes.put("name", "홍길동");

        return attributes;
    }

    private String determineRole(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email.endsWith("@school.edu")) return "TEACHER";
        return "USER";
    }
}


