package apt.auctionapi.entity.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AuctionHistory {
    private String auctionId;
    private String caseId;
    private String caseSite;
    private Integer itemNumber;
    private Integer historyOrder;
    private String appointedDayType;
    private LocalDateTime appointedDayAt;
    private Integer numberOfFailures;
    private Long lowestSellingPrice;
    private String results;
}
