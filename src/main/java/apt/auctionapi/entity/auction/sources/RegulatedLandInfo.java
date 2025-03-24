package apt.auctionapi.entity.auction.sources;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;

/**
 * 규제 토지 정보 (Regulated Land Info)
 * <p>
 * 이 클래스는 특정 사건과 관련된 토지 규제 정보를 포함합니다.
 * 규제 지역 및 토지 이용 제한 사항 등의 데이터를 포함합니다.
 */
@Getter
public class RegulatedLandInfo {

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
     * 토지 일련번호 (예: 1)
     */
    @Field("dspslObjctSeq")
    private Integer landSequence;

    /**
     * 토지 규제 상세 정보 (예: "도시지역, 제2종 일반주거지역")
     */
    @Field("rgltLandDtl")
    private String regulationDetails;
}
