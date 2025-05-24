package apt.auctionapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchAuctionLocationsRequest(
    @Schema(description = "좌측 하단 위도", example = "37.5709061")
    double lbLat,

    @Schema(description = "좌측 하단 경도", example = "126.6675837")
    double lbLng,

    @Schema(description = "우측 상단 위도", example = "37.601581")
    double rtLat,

    @Schema(description = "우측 상단 경도", example = "126.738909")
    double rtLng,

    @Schema(description = "진행", example = "true")
    Boolean isInProgress
) {

    public SearchAuctionLocationsRequest {
        if (isInProgress == null) {
            isInProgress = true;
        }
    }
}
