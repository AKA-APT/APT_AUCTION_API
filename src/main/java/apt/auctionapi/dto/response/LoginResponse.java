package apt.auctionapi.dto.response;

import apt.auctionapi.domain.SessionUser;

public record LoginResponse(
        String message,
        SessionUser user
) {
}
