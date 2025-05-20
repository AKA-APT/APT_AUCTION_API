package apt.auctionapi.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record InvestmentTagResponse(
    @Schema(description = "ID", example = "1")
    int id,
    @Schema(description = "이름", example = "수익형")
    String name,
    @Schema(description = "설명", example = "수익을 얻을 수 있는 투자 유형")
    String description
) {

}
