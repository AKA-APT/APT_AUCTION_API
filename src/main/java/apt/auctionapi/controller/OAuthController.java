package apt.auctionapi.controller;

import apt.auctionapi.domain.SessionUser;
import apt.auctionapi.controller.dto.response.LoginResponse;
import apt.auctionapi.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OAuth", description = "OAuth 인증 API")
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {

    @Value("${app.client-url}")
    private String clientUrl;

    private final OAuthService oAuthService;

    @Operation(summary = "카카오 로그인", description = "콜백 URL로부터 받은 인증 코드로 카카오 로그인을 수행합니다.")
    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam String code) {
        SessionUser sessionUser = oAuthService.kakaoLogin(code);

        return ResponseEntity.status(302)
            .header("Location", clientUrl)
            .body(new LoginResponse("Login successful", sessionUser));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인한 사용자를 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
