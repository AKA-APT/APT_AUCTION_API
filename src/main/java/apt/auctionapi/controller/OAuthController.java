package apt.auctionapi.controller;

import apt.auctionapi.service.OAuthService;
import apt.auctionapi.dto.ErrorResponse;
import apt.auctionapi.dto.LoginResponse;
import apt.auctionapi.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam String code) {
        SessionUser sessionUser = oAuthService.kakaoLogin(code);
        return ResponseEntity.ok(new LoginResponse("Login successful", sessionUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERROR", e.getMessage()));
    }
}
