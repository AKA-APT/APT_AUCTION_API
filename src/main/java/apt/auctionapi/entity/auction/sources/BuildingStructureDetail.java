package apt.auctionapi.entity.auction.sources;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 건물 구조 상세 정보 (Building Structure Detail)
 * <p>
 * 이 클래스는 특정 사건의 경매 대상 물건 중 건물 구조에 대한 세부 정보를 포함합니다.
 * 건물 구조의 재료, 층별 면적 등 건축 관련 정보를 포함합니다.
 */
@Getter
public class BuildingStructureDetail {

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
    private Integer objectSequence;

    /**
     * 건물 구조 일련번호 (예: 1)
     */
    @Field("bldSdtrSeq")
    private Integer structureSequence;

    /**
     * 건물 구조 상세 정보 (예: "철근콘크리트구조 (철근)콘크리트지붕 3층 단독주택")
     */
    @Field("bldSdtrDtlDts")
    private String structureDetails;
}
