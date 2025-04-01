package apt.auctionapi.entity.auction;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import apt.auctionapi.entity.auction.sources.AuctionObject;
import apt.auctionapi.entity.auction.sources.AuctionSchedule;
import apt.auctionapi.entity.auction.sources.CaseBaseInfo;
import apt.auctionapi.entity.auction.sources.DisposalGoodsExecutionInfo;
import apt.auctionapi.entity.auction.sources.EvaluationInfo;
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
     * 경매 진행 정보 (감정가, 낙찰가, 경매장 위치 등)
     */
    @Field("dspslGdsDxdyInfo")
    private DisposalGoodsExecutionInfo disposalGoodsExecutionInfo;

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
     * 감정 평가 정보 리스트
     */
    @Field("aeeWevlMnpntLst")
    private List<EvaluationInfo> evaluationList;

    /**
     * 경매 대상 물건 위도/경도 좌표
     */
    @Field("location")
    private GeoJsonPoint location;

    /**
     * 경매 취소/취하 여부 (취하되지 않았다면 null)
     */
    @JsonIgnore
    @Field("isAuctionCancelled")
    private Boolean isAuctionCancelled;

    /**
     * 현재 입찰가를 반환
     */
    public BigDecimal getLatestBiddingPrice() {
        return this.getDisposalGoodsExecutionInfo().getFirstAuctionPrice();
    }

    public void mappingCodeValues() {
        // 코드값 매핑
        if (getAuctionScheduleList() != null) {
            getAuctionScheduleList().forEach(auctionSchedule -> {
                if (auctionSchedule != null) {
                    if (auctionSchedule.getAuctionKindCode() != null) {
                        auctionSchedule.setAuctionKind(
                            AuctionCodeMapper.getAuctionKindDescription(auctionSchedule.getAuctionKindCode()));
                    }
                    if (auctionSchedule.getAuctionResultCode() != null) {
                        auctionSchedule.setAuctionResult(
                            AuctionCodeMapper.getAuctionResultDescription(auctionSchedule.getAuctionResultCode()));
                    }
                }
            });
        }

        // 코드값 매핑
        if (getEvaluationList() != null) {
            getEvaluationList().forEach(auctionEvaluation -> {
                if (auctionEvaluation != null) {
                    if (auctionEvaluation.getEvaluationCategoryCode() != null) {
                        auctionEvaluation.setEvaluationCategory(
                            AuctionCodeMapper.getEvaluationTableTypeDescription(
                                auctionEvaluation.getEvaluationCategoryCode()));
                    }
                    if (auctionEvaluation.getEvaluationItemCode() != null) {
                        auctionEvaluation.setEvaluationItem(
                            AuctionCodeMapper.getEvaluationItemDescription(auctionEvaluation.getEvaluationItemCode()));
                    }
                }
            });
        }

        // 코드값 매핑
        if (getDisposalGoodsExecutionInfo() != null) {
            String usageCode = getDisposalGoodsExecutionInfo().getAuctionGoodsUsageCode();
            if (usageCode != null) {
                getDisposalGoodsExecutionInfo()
                    .setAuctionGoodsUsage(AuctionCodeMapper.getAuctionGoodsUsageDescription(usageCode));
            }
        }
    }
}
