package apt.auctionapi.entity.auction.sources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

/**
 * 경매 일정 정보 (Auction Schedule)
 * <p>
 * 이 클래스는 특정 사건의 경매 일정과 관련된 정보를 포함합니다.
 * 경매 날짜, 시간, 장소 및 낙찰 가격 등의 데이터를 포함합니다.
 */
@Getter
public class AuctionSchedule {

    /**
     * 경매 진행 날짜 (예: "2025-02-27")
     */
    @Field("dxdyYmd")
    private LocalDate auctionDate;

    /**
     * 경매 진행 시간 (예: "10:00")
     */
    @Field("dxdyHm")
    private LocalTime auctionTime;

    /**
     * 경매 진행 장소 (예: "경매법정(4별관 211호)")
     */
    @Field("dxdyPlcNm")
    private String auctionPlace;

    /**
     * 경매 종류 코드 (예: "01" → 매각 기일)
     */
    @Field("auctnDxdyKndCd")
    private String auctionKindCode;

    @Setter
    private String auctionKind;

    /**
     * 경매 결과 코드 (예: "002" → 유찰)
     */
    @Field("auctnDxdyRsltCd")
    private String auctionResultCode;

    @Setter
    private String auctionResult;

    /**
     * 총 감정 평가 금액 (예: 2,382,979,600원)
     */
    @Field("tsLwsDspslPrc")
    private BigDecimal totalAuctionPrice;

    /**
     * 최종 낙찰가 (예: 0원 → 유찰)
     */
    @Field("dspslAmt")
    private BigDecimal finalAuctionPrice;
}
