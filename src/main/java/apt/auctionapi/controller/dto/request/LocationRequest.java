package apt.auctionapi.controller.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LocationRequest(
    @NotNull(message = "경도는 필수입니다")
    @Min(value = 124, message = "경도는 124도 이상이어야 합니다")
    @Max(value = 131, message = "경도는 131도 이하여야 합니다")
    Double longitude,

    @NotNull(message = "위도는 필수입니다")
    @Min(value = 33, message = "위도는 33도 이상이어야 합니다")
    @Max(value = 38, message = "위도는 38도 이하여야 합니다")
    Double latitude
) {

} 
