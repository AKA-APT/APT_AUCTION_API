package apt.auctionapi.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Document(collection = "auction_details")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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

    // Getters and Setters
}

class BjdInfo {
    private String sd;
    private String sgg;
    private String emd;
    private String bjdCode;
    private Location location;
    // Getters and Setters
}

class Location {
    private Double x;
    private Double y;
    // Getters and Setters
}

class AuctionHistory {
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
    // Getters and Setters
}

class AuctionObject {
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
    // Getters and Setters
}

class OccupantInfo {
    private String occupant;
    private LocalDateTime registrationDate;
    private Integer isOpposingPower;
    // Getters and Setters
}

class LandInfo {
    private String pnu;
    private String bjdCode;
    private String landPurposeName1;
    private String landUseName;
    private Double area;
    private List<UsagePlan> usagePlanItems;
    // Getters and Setters
}

class UsagePlan {
    private String landUsage;
    private String conflictType;
    // Getters and Setters
}
