package apt.auctionapi.controller.dto.response;

import apt.auctionapi.controller.dto.request.AuctionResponse;

public record TenderResponse(
        String auctionId,
        AuctionResponse auction,
        Long tenderCost
) {
}
