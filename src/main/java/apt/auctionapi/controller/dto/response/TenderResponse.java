package apt.auctionapi.controller.dto.response;

public record TenderResponse(
        String auctionId,
        AuctionResponse auction,
        Long tenderCost
) {
}
