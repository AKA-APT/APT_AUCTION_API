package apt.auctionapi.entity.auction_entity;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 임차 관계 및 주소 정보 (Rental Relation Info)
 * <p>
 * 이 클래스는 특정 사건의 경매 대상 물건과 관련된 임차 관계 및 주소 정보를 포함합니다.
 * 토지 대장 주소 및 도로명 주소 등을 포함합니다.
 */
@Getter
public class RentalRelationInfo {

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
     * 임차 관계 일련번호 (예: 1)
     */
    @Field("rletStSeq")
    private Integer rentalSequence;

    /**
     * 시/도 명 (예: "서울특별시")
     */
    @Field("adongSdNm")
    private String city;

    /**
     * 구/군 명 (예: "관악구")
     */
    @Field("adongSggNm")
    private String district;

    /**
     * 동/읍/면 명 (예: "신림동")
     */
    @Field("adongEmdNm")
    private String neighborhood;

    /**
     * 지번 주소 (예: "569-16")
     */
    @Field("rletStLtnoAddr")
    private String lotNumber;

    /**
     * 도로명 주소 (예: "남부순환로131길")
     */
    @Field("rdnm")
    private String roadName;

    /**
     * 도로명 건물 번호 (예: "40-4")
     */
    @Field("rdnmBldNo")
    private String roadBuildingNumber;
}
