package team.themoment.readygsm.domain.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.themoment.readygsm.domain.user.data.constant.UserRole;
import team.themoment.readygsm.domain.user.presentation.data.response.GetUserResDto;
import team.themoment.readygsm.domain.user.service.FindUserService;
import team.themoment.readygsm.domain.user.service.SearchUserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final FindUserService findUserService;
    private final SearchUserService searchUserService;

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResDto> getUser(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(findUserService.execute(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<GetUserResDto> getCurrentUser() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new GetUserResDto(
                1L, "Test User", "s00000@gsm.hs.kr", UserRole.USER, new ArrayList<>()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GetUserResDto>> searchUsers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "role", required = false) UserRole role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "255") int limit
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(searchUserService.execute(name, email, role, page, limit));
    }
}