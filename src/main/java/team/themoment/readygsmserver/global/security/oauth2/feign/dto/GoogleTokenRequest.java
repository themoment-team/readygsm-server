package team.themoment.readygsmserver.global.security.oauth2.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenRequest(
        @JsonProperty("code") String code,
        @JsonProperty("client_id") String clientId,
        @JsonProperty("client_secret") String clientSecret,
        @JsonProperty("redirect_uri") String redirectUri,
        @JsonProperty("grant_type") String grantType
) {
}
