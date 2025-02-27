package apt.auctionapi.controller;

import apt.auctionapi.auth.AuthMember;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.AuctionSummary;
import apt.auctionapi.service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "경매", description = "경매 목록 조회 및 상세 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @Operation(summary = "경매 목록 조회", description = "지정한 범위 내의 경매 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AuctionSummaryGroupedResponse>> getAuctionsByLocation(
            @Schema(description = "좌측 하단 위도", example = "37.5808292")
            @RequestParam double lbLat,
            @Schema(description = "좌측 하단 경도", example = "126.6969164")
            @RequestParam double lbLng,
            @Schema(description = "우측 상단 위도", example = "37.5913546")
            @RequestParam double rtLat,
            @Schema(description = "우측 상단 경도", example = "126.7060359")
            @RequestParam double rtLng
    ) {
        return ResponseEntity.ok(
                auctionService.getAuctionsByLocationRange(lbLat, lbLng, rtLat, rtLng)
        );
    }

    @Operation(summary = "경매 상세 조회", description = "지정한 경매 ID에 해당하는 경매의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable String id) {
        return ResponseEntity.ok(auctionService.getAuctionById(id));
    }

    @Operation(summary = "좋아요", description = "사용자가 경매를 좋아요합니다.")
    @GetMapping("/interests/{id}")
    public ResponseEntity<Void> interestAuction(
            @AuthMember Member member,
            @Schema(description = "경매 ID", example = "60f1b3b3b3b3b3b3b3b3b3b3")
            @PathVariable String id
    ) {
        auctionService.interestAuction(member, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요한 목록", description = "사용자가 좋아요한 경매 목록을 조회합니다.")
    @GetMapping("/interests")
    public ResponseEntity<List<AuctionSummary>> getInterestAuctions(
            @AuthMember Member member
    ) {
        return ResponseEntity.ok(auctionService.getInterestedAuctions(member));
    }
}
