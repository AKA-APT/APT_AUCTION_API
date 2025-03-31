package apt.auctionapi.entity.auction;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import apt.auctionapi.entity.auction.sources.AdditionalBuildingInfo;
import apt.auctionapi.entity.auction.sources.AroundDisposalStatistics;
import apt.auctionapi.entity.auction.sources.AuctionObject;
import apt.auctionapi.entity.auction.sources.AuctionPhotoInfo;
import apt.auctionapi.entity.auction.sources.AuctionSchedule;
import apt.auctionapi.entity.auction.sources.BuildingStructureDetail;
import apt.auctionapi.entity.auction.sources.CaseBaseInfo;
import apt.auctionapi.entity.auction.sources.DisposalGoodsExecutionInfo;
import apt.auctionapi.entity.auction.sources.DistrictDemandInfo;
import apt.auctionapi.entity.auction.sources.EvaluationInfo;
import apt.auctionapi.entity.auction.sources.RegulatedLandInfo;
import apt.auctionapi.entity.auction.sources.RentalRelationInfo;
import lombok.Getter;

/**
 * 경매 데이터 엔티티 (Auction Entity)
 * <p>
 * 이 클래스는 MongoDB 컬렉션 "auctions"에 저장된 경매 데이터를 매핑하는 엔티티입니다.
 * 사건 기본 정보, 경매 일정, 감정 평가, 주변 통계 등 경매와 관련된 모든 데이터를 포함합니다.
 */
@Getter
@Document(collection = "auctions")
public class Auction {

    /**
     * MongoDB 문서의 고유 ID
     */
    @Id
    private String id;

    /**
     * 사건 기본 정보 (법원, 사건 번호, 사건 종류 등)
     */
    @Field("csBaseInfo")
    private CaseBaseInfo caseBaseInfo;

    /**
     * 집행문 발부 정보 리스트
     */
    @Field("dstrtDemnInfo")
    private List<DistrictDemandInfo> districtDemandInfoList;

    /**
     * 경매 진행 정보 (감정가, 낙찰가, 경매장 위치 등)
     */
    @Field("dspslGdsDxdyInfo")
    private DisposalGoodsExecutionInfo disposalGoodsExecutionInfo;

    /**
     * 경매 관련 사진 정보 리스트
     */
    @Field("picDvsIndvdCnt")
    private List<AuctionPhotoInfo> photoInfoList;

    /**
     * 경매 일정 정보 리스트
     */
    @Field("gdsDspslDxdyLst")
    private List<AuctionSchedule> auctionScheduleList;

    /**
     * 경매 대상 물건 정보 리스트
     */
    @Field("gdsDspslObjctLst")
    private List<AuctionObject> auctionObjectList;

    /**
     * 규제 토지 정보 리스트
     */
    @Field("rgltLandLstAll")
    private List<List<RegulatedLandInfo>> regulatedLandList;

    /**
     * 건물 구조 상세 정보 리스트
     */
    @Field("bldSdtrDtlLstAll")
    private List<List<BuildingStructureDetail>> buildingStructureDetailList;

    /**
     * 제시외 건물 정보 리스트
     */
    @Field("gdsNotSugtBldLsstAll")
    private List<List<AdditionalBuildingInfo>> additionalBuildingList;

    /**
     * 임차 관계 및 주소 정보 리스트
     */
    @Field("gdsRletStLtnoLstAll")
    private List<List<RentalRelationInfo>> rentalRelationList;

    /**
     * 감정 평가 정보 리스트
     */
    @Field("aeeWevlMnpntLst")
    private List<EvaluationInfo> evaluationList;

    /**
     * 주변 경매 통계 정보 리스트
     */
    @Field("aroundDspslStats")
    private List<AroundDisposalStatistics> aroundDisposalStats;

    /**
     * 경매 대상 물건 위도/경도 좌표
     */
    @Field("location")
    private GeoJsonPoint location;

    /**
     * 경매 취소/취하 여부 (취하되지 않았다면 null)
     */
    @Field("isAuctionCancelled")
    private Boolean isAuctionCancelled;

    /**
     * 현재 입찰가를 반환
     */
    public BigDecimal getLatestBiddingPrice() {
        if (disposalGoodsExecutionInfo.getFourthAuctionTime() != null) {
            return disposalGoodsExecutionInfo.getFourthAuctionPrice();
        }
        if (disposalGoodsExecutionInfo.getThirdAuctionTime() != null) {
            return disposalGoodsExecutionInfo.getThirdAuctionPrice();
        }
        if (disposalGoodsExecutionInfo.getSecondAuctionTime() != null) {
            return disposalGoodsExecutionInfo.getSecondAuctionPrice();
        }
        if (disposalGoodsExecutionInfo.getFirstAuctionTime() != null) {
            return disposalGoodsExecutionInfo.getFirstAuctionPrice();
        }
        return disposalGoodsExecutionInfo.getAppraisedValue();
    }

    public void mappingCodeValues() {
        // 코드값 매핑
        getAuctionScheduleList().forEach(auctionSchedule -> {
            auctionSchedule.setAuctionKind(
                AuctionCodeMapper.getAuctionKindDescription(auctionSchedule.getAuctionKindCode()));
            auctionSchedule.setAuctionResult(
                AuctionCodeMapper.getAuctionResultDescription(auctionSchedule.getAuctionResultCode()));
        });

        // 코드값 매핑
        getEvaluationList().forEach(auctionEvaluation -> {
            auctionEvaluation.setEvaluationCategory(
                AuctionCodeMapper.getEvaluationTableTypeDescription(auctionEvaluation.getEvaluationCategoryCode()));
            auctionEvaluation.setEvaluationItem(
                AuctionCodeMapper.getEvaluationItemDescription(auctionEvaluation.getEvaluationItemCode()));
        });

        // 코드값 매핑
        String usageCode = getDisposalGoodsExecutionInfo().getAuctionGoodsUsageCode();
        getDisposalGoodsExecutionInfo()
            .setAuctionGoodsUsage(AuctionCodeMapper.getAuctionGoodsUsageDescription(usageCode));
    }
}
