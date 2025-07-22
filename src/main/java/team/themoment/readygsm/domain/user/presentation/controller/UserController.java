package team.themoment.readygsm.domain.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.presentation.data.response.GetUserResDto;
import team.themoment.readygsm.domain.user.service.FindUserService;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final FindUserService findUserService;

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResDto> getUser(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(findUserService.execute(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<GetUserResDto> getCurrentUser() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new GetUserResDto(
                1L, "Test User", "s00000@gsm.hs.kr", UserRole.USER, new ArrayList<>()));
    }
}