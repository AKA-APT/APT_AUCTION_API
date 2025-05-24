package apt.auctionapi.entity;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Document(collection = "auctions_richgo")
public class AuctionDetail {

    @Id
    private String id;

    @Field("auction_id")
    private ObjectId auctionId;

    private int photoCount;

    private List<RightHolderGroup> rightHolders;

    private LandInfo landInfo;

    private String keyword;

    private List<OccupantInfo> occupantInfoList;

    @Getter
    public static class RightHolderGroup {
        private String objectType;
        private List<RightHolderDetail> rightHolders;
    }

    @Getter
    public static class RightHolderDetail {
        private String owner;
        private String debtor;
        private List<String> creditors;
    }

    @Getter
    public static class LandInfo {
        private List<PublishedPrice> publishedPriceList;
    }

    @Getter
    public static class PublishedPrice {
        private String pnu;
        private int stdYear;
        private double totalPublishedPrice;
        private double publishedPrice;
        private double sdPublishedPrice;
        private double sggPublishedPrice;
        private double emdPublishedPrice;
    }

    @Getter
    public static class OccupantInfo {
        private String auctionId;
        private String caseId;
        private String caseSite;
        private int itemNumber;
        private int occupantNumber;
        private String occupant;
        private String occupancyPart;
        private LocalDate registrationDate;
        private LocalDate fixedDate;
        private LocalDate dividendDemandDate;
        private Double deposit;
        private Double leasePrice;
        private String leasePriceType;
        private Double expectedDividends;
        private Integer isOpposingPower;
        private LocalDate dividendRequestTerminationDt;
        private Double simpleDeposit;
        private Double simpleLeasePrice;
        private Double simpleExpectedDividends;
    }
}