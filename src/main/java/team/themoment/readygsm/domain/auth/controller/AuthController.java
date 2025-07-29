package team.themoment.readygsm.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.readygsm.domain.auth.dto.AuthRequest;
import team.themoment.readygsm.domain.auth.dto.AuthResponse;
import team.themoment.readygsm.domain.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> authorizeWithCode(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticateWithCode(request.getCode());
        return ResponseEntity.ok(response);
    }
}

