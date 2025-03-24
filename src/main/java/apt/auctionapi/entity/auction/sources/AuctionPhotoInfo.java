package apt.auctionapi.entity.auction.sources;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;

/**
 * 경매 사진 정보 (Auction Photo Info)
 * <p>
 * 이 클래스는 특정 경매 사건과 관련된 사진 정보를 나타냅니다.
 * 사진의 카테고리 코드와 사진 개수를 포함합니다.
 */
@Getter
public class AuctionPhotoInfo {

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
     * 사진 유형 코드 (예: "000244" → 외관 사진)
     */
    @Field("cortAuctnPicDvsCd")
    private String photoCategoryCode;

    /**
     * 해당 유형의 사진 개수 (예: 2장)
     */
    @Field("photoGubunCnt")
    private Integer photoCount;
}
