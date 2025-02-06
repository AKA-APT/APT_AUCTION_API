package apt.auctionapi.controller;

import apt.auctionapi.domain.SessionUser;
import apt.auctionapi.dto.response.LoginResponse;
import apt.auctionapi.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final OAuthService oAuthService;

    @GetMapping("")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam String code) {
        SessionUser sessionUser = oAuthService.kakaoLogin(code);
        return ResponseEntity.ok(new LoginResponse("Login successful", sessionUser));
    }
}
