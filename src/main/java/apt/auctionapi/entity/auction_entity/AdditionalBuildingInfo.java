package apt.auctionapi.entity.auction_entity;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * 제시외 건물 정보 (Additional Building Info)
 * <p>
 * 이 클래스는 특정 사건의 경매 대상 물건 중 제시외 건물(본 건물과 별개로 존재하는 부속 건물)에 대한 정보를 포함합니다.
 * 건물의 사용 용도, 건축 구조, 평가 금액 등의 데이터를 포함합니다.
 */
@Getter
public class AdditionalBuildingInfo {

    /**
     * 법원 코드 (예: "B000210")
     */
    @Field("cortOfcCd")
    private String courtCode;

    /**
     * 사건 번호 (예: "20240130112651")
     */
    @Field("csNo")
    private String caseNumber;

    /**
     * 경매 대상 물건의 일련번호 (예: 2)
     */
    @Field("dspslObjctSeq")
    private int objectSequence;

    /**
     * 제시외 건물 일련번호 (예: 1)
     */
    @Field("sugtBsdsBldSeq")
    private Integer additionalBuildingSequence;

    /**
     * 사용 용도 (예: "보일러실")
     */
    @Field("etcUsgCtt")
    private String usageDetails;

    /**
     * 건축 구조 상세 정보 (예: "판넬조")
     */
    @Field("bldStrcDts")
    private String structureDetails;

    /**
     * 건물 면적 (예: "2.4㎡")
     */
    @Field("bldArDts")
    private String buildingArea;

    /**
     * 감정 평가 금액 (예: 1,200,000원)
     */
    @Field("evlAmt")
    private BigDecimal evaluatedAmount;

    /**
     * 비고 사항 (예: "옥상소재")
     */
    @Field("sugtBsdsBldRmk")
    private String remarks;
}
