package apt.auctionapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apt.auctionapi.domain.SessionUser;
import apt.auctionapi.dto.response.LoginResponse;
import apt.auctionapi.entity.Auction;
import apt.auctionapi.service.AuctionService;
import apt.auctionapi.service.OAuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final OAuthService oAuthService;

    @GetMapping("/api/v1/map/auctions")
    public ResponseEntity<List<Auction>> getAuctions(
        @RequestParam double minLat,
        @RequestParam double minLon,
        @RequestParam double maxLat,
        @RequestParam double maxLon) {
        List<Auction> auctions = auctionService.findAuctionsWithinBounds(minLat, minLon, maxLat, maxLon);
        return ResponseEntity.ok(auctions);
    }

    @GetMapping("/api/v1/auctions")
    public ResponseEntity<LoginResponse> kakaoCallback(@RequestParam String code) {
        SessionUser sessionUser = oAuthService.kakaoLogin(code);
        return ResponseEntity.ok(new LoginResponse("Login successful", sessionUser));
    }
}
