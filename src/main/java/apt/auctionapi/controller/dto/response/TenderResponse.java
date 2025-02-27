package apt.auctionapi.controller.dto.response;

import apt.auctionapi.entity.auction.Auction;

public record TenderResponse(
        String auctionId,
        Auction auction,
        Long tenderCost
) {
}
