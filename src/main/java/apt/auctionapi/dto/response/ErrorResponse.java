package apt.auctionapi.dto.response;

public record ErrorResponse(
        String code,
        String message
) {
}
