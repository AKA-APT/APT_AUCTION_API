package apt.auctionapi.entity;

import apt.auctionapi.entity.auction.*;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "auction_details")
@Getter
public class Auction {
    @Id
    private String id;
    private String keyword;
    private LocalDateTime biddingDate;
    private String pnu;
    private String bjdCode;
    private BjdInfo bjdInfo;
    private LocalDate registrationDate;
    private LocalDate sellingDate;
    private String category;
    private String danjiId;
    private Integer pyeong;
    private Long appraisedPrice;
    private Long lowestSellingPrice;
    private Long sellingPrice;
    private Integer numberOfFailures;
    private Integer biddingDepositMin;
    private Integer biddingDepositMax;
    private Integer biddingDepositPercentMin;
    private Integer biddingDepositPercentMax;
    private String itemStatus;
    private List<AuctionHistory> historyList;
    private List<AuctionObject> objectList;
    private List<OccupantInfo> occupantInfoList;
    private LandInfo landInfo;
}
