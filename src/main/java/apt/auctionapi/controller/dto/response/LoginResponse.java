package apt.auctionapi.controller.dto.response;

import apt.auctionapi.domain.SessionUser;

public record LoginResponse(
        String message,
        SessionUser user
) {
}
