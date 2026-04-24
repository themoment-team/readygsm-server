package team.themoment.readygsmserver.domain.auth.dto;

import team.themoment.readygsmserver.domain.user.entity.constant.AuthReferrerType;

public record UserAuthInfo(String email, String provider, AuthReferrerType authReferrerType) {
}
