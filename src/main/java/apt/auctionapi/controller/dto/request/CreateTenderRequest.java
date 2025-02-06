package apt.auctionapi.controller.dto.request;

public record CreateTenderRequest(
        String auctionId,
        Long amount
) {
}
