package team.themoment.readygsm.domain.auth.provider;

import org.springframework.stereotype.Component;
import team.themoment.readygsm.global.security.oauth.OAuthUserInfo;

import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthProvider {

    public OAuthUserInfo getUserInfo(String code) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("name", "홍길동");

        return new OAuthUserInfo(attributes);
    }
}

