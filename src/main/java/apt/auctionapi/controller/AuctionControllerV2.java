package apt.auctionapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.entity.AuctionEntity;
import apt.auctionapi.service.AuctionServiceV2;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/auctions")
public class AuctionControllerV2 {

    private final AuctionServiceV2 auctionService;

    @GetMapping
    public List<AuctionSummaryGroupedResponse> getAuctionsByLocation(
        @RequestParam double lbLat,
        @RequestParam double lbLng,
        @RequestParam double rtLat,
        @RequestParam double rtLng) {
        return auctionService.getAuctionsByLocationRange(lbLat, lbLng, rtLat, rtLng);
    }

    @GetMapping("/{id}")
    public AuctionEntity getAuctionById(@PathVariable String id) {
        return auctionService.getAuctionById(id);
    }
}
