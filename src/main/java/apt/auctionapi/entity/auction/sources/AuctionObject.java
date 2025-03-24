package apt.auctionapi.entity.auction.sources;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

/**
 * 경매 대상 물건 정보 (Auction Object)
 * <p>
 * 이 클래스는 특정 사건에 대한 경매 대상 물건 정보를 포함합니다.
 * 물건의 감정가, 유형, 건물 구조, 위치 등의 데이터를 포함합니다.
 */
@Getter
public class AuctionObject {

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
     * 경매 대상 물건의 일련번호 (예: 1)
     */
    @Field("dspslGdsSeq")
    private Integer objectSequence;

    /**
     * 물건의 종류 (예: "일반건물")
     */
    @Field("rletDvsDts")
    private String propertyType;

    /**
     * 건물 구조 (예: "철근콘크리트구조")
     */
    @Field("pjbBuldList")
    private String buildingStructure;

    /**
     * 감정 평가 금액 (예: 1,745,896,000원)
     */
    @Field("aeeEvlAmt")
    private BigDecimal appraisedValue;

    /**
     * 토지 이용 코드 (예: "10000")
     */
    @Field("lclDspslGdsLstUsgCd")
    private String landUseCode;

    /**
     * 경매 대상 물건의 위도 (예: 38.379562564914785)
     */
    @Setter
    private Double latitude;

    /**
     * 경매 대상 물건의 경도 (예: 128.18470890620853)
     */
    @Setter
    private Double longitude;

    /**
     * 물건의 주소 (예: "서울특별시 관악구 신림동 569-16")
     */
    @Field("userPrintSt")
    private String address;
}
