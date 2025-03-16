package apt.auctionapi.controller;

import apt.auctionapi.controller.dto.response.AuctionStatusResponse;
import apt.auctionapi.service.AuctionStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "경매", description = "경매 목록 조회 및 상세 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auctions")
public class AuctionStatusController {

    private final AuctionStatusService auctionStatusService;

    /**
     * 특정 경매의 낙찰 상태 조회 API
     */
    @Operation(summary = "경매 상태 조회", description = "지정한 경매 ID에 해당하는 경매 상태를 조회합니다.")
    @GetMapping("/{id}/status")
    public ResponseEntity<AuctionStatusResponse> getAuctionStatus(@PathVariable String id) {
        return ResponseEntity.ok(auctionStatusService.getAuctionStatus(id));
    }
}
