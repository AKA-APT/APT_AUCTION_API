package apt.auctionapi.dto;

public record ErrorResponse(
        String code,
        String message
) {}
