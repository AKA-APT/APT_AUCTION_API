package apt.auctionapi.controller.dto.response;

import apt.auctionapi.entity.auction.Auction;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "입찰 응답 정보")
public record TenderResponse(
    @Schema(description = "경매 식별자", example = "AUC12345678")
    String auctionId,

    @Schema(description = "경매 정보", implementation = Auction.class)
    Auction auction,

    @Schema(description = "입찰 비용", example = "50000")
    Long tenderCost,

    @Schema(description = "경매 상태 정보")
    AuctionStatusResponse auctionStatus
) {
}
