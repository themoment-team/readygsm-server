package team.themoment.readygsmserver.domain.auth.dto.request;

public record OAuthCodeRequest(String code, String state) {
}
