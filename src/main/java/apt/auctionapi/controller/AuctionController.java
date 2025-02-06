package apt.auctionapi.controller;

import apt.auctionapi.controller.dto.request.AuctionResponse;
import apt.auctionapi.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getAuctions(
            @RequestParam Double lbLat,
            @RequestParam Double lbLon,
            @RequestParam Double rtLat,
            @RequestParam Double rtLon
    ) {
        List<AuctionResponse> auctions = auctionService.findAuctionsWithinBounds(lbLat, lbLon, rtLat, rtLon);
        return ResponseEntity.ok(auctions);
    }
}
