package apt.auctionapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import apt.auctionapi.service.AuctionImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "경매", description = "경매 목록 조회 및 상세 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auctions")
public class AuctionImageController {

    private final AuctionImageService auctionImageService;

    @Operation(summary = "경매 이미지 조회", description = "지정한 경매 ID에 해당하는 경매의 이미지 리스트를 조회합니다.")
    @GetMapping("/{id}/images")
    public JsonNode getAuctionImages(@PathVariable String id) {
        return auctionImageService.getAuctionImages(id);
    }
}
