package apt.auctionapi.entity.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BjdInfo {
    private String sd;
    private String sgg;
    private String emd;
    private String bjdCode;
    private Location location;
}
