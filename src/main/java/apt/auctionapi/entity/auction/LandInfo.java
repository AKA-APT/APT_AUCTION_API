package apt.auctionapi.entity.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LandInfo {
    private String pnu;
    private String bjdCode;
    private String landPurposeName;
    private String landUseName;
    private Double area;
    private List<UsagePlan> usagePlanItems;
}
