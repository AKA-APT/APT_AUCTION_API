package apt.auctionapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apt.auctionapi.entity.Auction;
import apt.auctionapi.service.AuctionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping
    public ResponseEntity<List<Auction>> getAuctions(
        @RequestParam double minLat,
        @RequestParam double minLon,
        @RequestParam double maxLat,
        @RequestParam double maxLon) {
        List<Auction> auctions = auctionService.findAuctionsWithinBounds(minLat, minLon, maxLat, maxLon);
        return ResponseEntity.ok(auctions);
    }
}