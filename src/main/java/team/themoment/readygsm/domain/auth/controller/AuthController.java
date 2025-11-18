package team.themoment.readygsm.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.readygsm.domain.auth.dto.SignInReqDto;
import team.themoment.readygsm.domain.auth.dto.AuthResponse;
import team.themoment.readygsm.domain.auth.service.SignInService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignInService authService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authorizeWithCode(@RequestBody SignInReqDto request) {
        // SignInService의 변경된 메서드 이름(authenticateWithCode) 사용
        AuthResponse response = authService.authenticateWithCode(request.getCode());
        return ResponseEntity.ok(response);
    }
}

