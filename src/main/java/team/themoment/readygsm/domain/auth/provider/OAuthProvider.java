package team.themoment.readygsm.domain.auth.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.themoment.readygsm.global.security.oauth.OAuthUserInfo;


import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuthProvider {


    private final RestTemplate restTemplate;

    private final ClientRegistrationRepository clientRegistrationRepository;

    private static final String PROVIDER_ID = "google";

    public OAuthUserInfo getUserInfo(String code) {
        String accessToken = getAccessToken(code);
        Map<String, Object> userAttributes = getUserAttributes(accessToken);
        String email = (String) userAttributes.get("email");
        String name = (String) userAttributes.get("name");

        return new OAuthUserInfo(userAttributes);
    }

    private String getAccessToken(String code) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(PROVIDER_ID);
        if (clientRegistration == null) {
            throw new IllegalStateException("ClientRegistration for " + PROVIDER_ID + " not found.");
        }

        String clientId = clientRegistration.getClientId();
        String clientSecret = clientRegistration.getClientSecret();
        String redirectUri = clientRegistration.getRedirectUri();
        String tokenUrl = clientRegistration.getProviderDetails().getTokenUri();

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
                .substring(1);

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
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(PROVIDER_ID);
        if (clientRegistration == null) {
            throw new IllegalStateException("ClientRegistration for " + PROVIDER_ID + " not found.");
        }

        String userInfoUrl = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();

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
