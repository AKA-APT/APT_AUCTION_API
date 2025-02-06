package apt.auctionapi.entity.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuctionObject {
    private String auctionId;
    private String caseId;
    private String caseSite;
    private Integer itemNumber;
    private Integer objectNumber;
    private String objectType;
    private String usage;
    private String objectAddress;
    private Double groundTotalArea;
    private Double buildingTotalArea;
}
