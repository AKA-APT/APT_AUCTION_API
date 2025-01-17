package apt.auctionapi.dto;

public record LoginResponse(
        String message,
        SessionUser user
) {}
