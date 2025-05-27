package apt.auctionapi.entity;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

@Getter
@Document(collection = "auctions_richgo")
public class AuctionDetail {

    @Id
    private String id;

    @Field("auction_id")
    @JsonIgnore
    private ObjectId auctionId;

    private String keyword;

    private int photoCount;

    private List<RightHolderGroup> rightHolders;

    private List<OccupancyInfo> occupancyInfoList;

    private List<OccupancyRelationship> occupancyRelationshipList;

    private List<OccupantInfo> occupantInfoList;

    private List<AuctionObject> objectList;

    @Field("landInfo.publishedPriceList")
    private List<PublishedPrice> publishedPriceList;

    @Field("landInfo.landInfo")
    private LandInfo landInfo;

    @Field("landInfo.buildingInfo")
    private BuildingInfo buildingInfo;

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
    public static class OccupantInfo {
        private String auctionId;
        private String caseId;
        private String caseSite;
        private Integer itemNumber;
        private Integer occupantNumber;
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

    @Getter
    public static class AuctionObject {
        private String auctionId;
        private String caseId;
        private String caseSite;
        private Integer itemNumber;
        private Integer objectNumber;
        private String objectType;
        private String usage;
        private String area;
        private String objectAddress;
        private String groundTotalArea;
        private String buildingTotalArea;
        private String otherBuildingTotalArea;
        private String objectDetail;
        private Integer isPartSell;
    }

    @Getter
    public static class PublishedPrice {
        private String pnu;
        private Integer stdYear;
        private Double totalPublishedPrice;
        private Double publishedPrice;
        private Double sdPublishedPrice;
        private Double sggPublishedPrice;
        private Double emdPublishedPrice;
    }

    @Getter
    public static class LandInfo {
        private String pnu;
        private String bjdCode;
        private BjdInfo bjdInfo;
        private String addressBjd;
        private String addressRoad;
        private String bunji;
        private String landCategoryName;
        private String landPurposeName1;
        private String landPurposeName2;
        private String landUseName;
        private String heightName;
        private String landShapeName;
        private String roadSideName;
        private Integer years;
        private Double area;
        private Double TPUP;
        private Double PUP;
        private Integer BD;
        private Integer DBD;
        private Double grossAreaSum;
        private Integer maxFloorArea;
        private Integer maxBuildingRatio;
    }

    @Getter
    public static class BjdInfo {
        private String sd;
        private String sgg;
        private String emd;
        private String dr;
        private String bjdCode;
    }

    @Getter
    public static class BuildingInfo {
        private Object masterConstructionInfo;
        private List<MasterBuildingInfo> masterBuildingInfoList;
    }

    @Getter
    public static class MasterBuildingInfo {
        private String buildingMasterId;
        private String danji;
        private String building;
        private String pnu;
        private String bjdCode;
        private LocalDate constructDate;
        private Double grossArea;
        private Integer faEstimateToGross;
        private Double floorArea;
        private Double constructArea;
        private Double buildingRatio;
        private String mainPurposeName;
        private String mainPurposeIndex;
        private String etcPurpose;
        private String mainOrPartDivName;
        private Integer mechParking;
        private Integer selfParking;
        private Integer groundFloorCnt;
        private Integer undergroundFloorCnt;
        private String roofName;
        private String mainStructureMaterial;
        private Integer elevatorCnt;
        private Integer emergencyElevatorCnt;
        private List<FloorInfo> floorInfoList;
    }

    @Getter
    public static class FloorInfo {
        private String buildingMasterId;
        private String floorType;
        private Integer floor;
        private Double area;
        private String mainOrPartDivName;
        private String mainPurposeName;
        private String etcPurposeName;
        private String mainStructureMaterial;
        private String etcStructureMaterial;
    }

    @Getter
    public static class OccupancyInfo {
        private String auctionId;
        private String caseId;
        private String caseSite;
        private Integer itemNumber;
        private Integer objectNumber;
        private Integer occupantNumber;
        private String objectAddress;
        private String occupant;
        private String occupantType;
        private String occupancyPart;
        private String occupancyUsage;
        private String occupancyPeriod;
        private Double deposit;
        private Double leasePrice;
        private String leasePriceType;
        private LocalDate registrationDt;
        private LocalDate fixedDt;
        private Double simpleDeposit;
        private Double simpleLeasePrice;
    }

    @Getter
    public static class OccupancyRelationship {
        private String auctionId;
        private String caseId;
        private String caseSite;
        private Integer itemNumber;
        private Integer objectNumber;
        private String objectAddress;
        private String occupancyRelationship;
        private String occupancyRelationshipInfo;
        private String status;
    }
}
