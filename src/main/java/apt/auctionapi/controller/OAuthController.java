package apt.auctionapi.controller;

import apt.auctionapi.domain.SessionUser;
import apt.auctionapi.controller.dto.response.LoginResponse;
import apt.auctionapi.service.OAuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {

    @Value("${app.client-url}")
    private String clientUrl;

    private final OAuthService oAuthService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam String code) {
        SessionUser sessionUser = oAuthService.kakaoLogin(code);

        return ResponseEntity.status(302)
            .header("Location", clientUrl)
            .body(new LoginResponse("Login successful", sessionUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
