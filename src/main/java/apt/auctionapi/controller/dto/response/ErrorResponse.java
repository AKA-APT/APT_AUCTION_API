package apt.auctionapi.controller.dto.response;

public record ErrorResponse(
    String code,
    String message
) {
}
