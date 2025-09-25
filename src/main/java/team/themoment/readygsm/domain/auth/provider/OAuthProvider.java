package team.themoment.readygsm.domain.auth.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.themoment.readygsm.global.security.oauth.OAuthUserInfo;


import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuthProvider {

    @Value("${CLIENTS_ID}")
    private String clientId;

    @Value("${CLIENTS_PASSWORD}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    public OAuthUserInfo getUserInfo(String code) {
        String accessToken = getAccessToken(code);
        Map<String, Object> userAttributes = getUserAttributes(accessToken);

        String email = (String) userAttributes.get("email");
        String name = (String) userAttributes.get("name");


        return new OAuthUserInfo(userAttributes);

    }

    private String getAccessToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = UriComponentsBuilder.newInstance()
                .queryParam("code", code)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("grant_type", "authorization_code")
                .build()
                .toUriString()
                .substring(1); // 맨 앞에 붙는 ? 제거

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || responseBody.get("access_token") == null) {
            throw new IllegalStateException("Access token을 받아오지 못했습니다.");
        }

        return (String) responseBody.get("access_token");
    }

    private Map<String, Object> getUserAttributes(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                Map.class
        );

        Map<String, Object> userAttributes = response.getBody();
        if (userAttributes == null) {
            throw new IllegalStateException("사용자 정보를 받아오지 못했습니다.");
        }

        return userAttributes;
    }

    private String determineDefaultRole() {
        return "USER";
    }
}
