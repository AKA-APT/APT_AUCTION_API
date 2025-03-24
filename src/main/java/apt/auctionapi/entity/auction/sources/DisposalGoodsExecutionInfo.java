package apt.auctionapi.entity.auction.sources;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 경매 진행 정보 (Disposal Goods Execution Info)
 * <p>
 * 이 클래스는 특정 사건의 경매 물건에 대한 진행 정보를 나타냅니다.
 * 경매 상태, 평가 금액, 경매 장소 및 날짜 등의 데이터를 포함합니다.
 */
@Getter
public class DisposalGoodsExecutionInfo {

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
    private Integer disposalGoodsSequence;

    /**
     * 경매 물건 상태 코드 (예: "01" → 진행 중)
     */
    @Field("auctnGdsStatCd")
    private String auctionGoodsStatus;

    /**
     * 경매 물건의 세부 사항 작성일 (예: "2025-02-12")
     */
    @Field("gdsSpcfcWrtYmd")
    private LocalDate goodsSpecificationDate;

    /**
     * 경매 물건에 대한 비고 사항 (예: "일괄매각. 제시외 건물 포함")
     */
    @Field("gdsSpcfcRmk")
    private String remarks;

    /**
     * 우선순위 근저당 설정 정보 (예: "2017.11.09. 근저당권(토지)")
     */
    @Field("tprtyRnkHypthcStngDts")
    private String mortgageDetails;

    /**
     * 경매 물건 관련 추가 비고 (예: "일괄매각. 제시외 건물 포함")
     */
    @Field("dspslGdsRmk")
    private String additionalRemarks;

    /**
     * 경매 물건 사용 코드 (예: "11")
     */
    @Field("auctnGdsUsgCd")
    private String auctionGoodsUsageCode;

    @Setter
    private String auctionGoodsUsage;

    /**
     * 해당 건물의 총 층 수 (예: 2층)
     */
    @Field("flbdNcnt")
    private int floorCount;

    /**
     * 감정 평가 금액 (예: 2,382,979,600원)
     */
    @Field("aeeEvlAmt")
    private BigDecimal appraisedValue;

    /**
     * 최초 경매 시작 가격 (예: 1,525,107,000원)
     */
    @Field("fstPbancLwsDspslPrc")
    private BigDecimal firstAuctionPrice;

    /**
     * 두 번째 경매 시작 가격 (예: null → 진행되지 않음)
     */
    @Field("scndPbancLwsDspslPrc")
    private BigDecimal secondAuctionPrice;

    /**
     * 세 번째 경매 시작 가격 (예: null → 진행되지 않음)
     */
    @Field("thrdPbancLwsDspslPrc")
    private BigDecimal thirdAuctionPrice;

    /**
     * 네 번째 경매 시작 가격 (예: null → 진행되지 않음)
     */
    @Field("fothPbancLwsDspslPrc")
    private BigDecimal fourthAuctionPrice;

    /**
     * 입찰 구분 코드 (예: "000331")
     */
    @Field("bidDvsCd")
    private String bidTypeCode;

    /**
     * 경매 진행 날짜 (예: "2025-02-27")
     */
    @Field("dspslDxdyYmd")
    private LocalDate auctionDate;

    /**
     * 첫 번째 경매 진행 시간 (예: "10:00")
     */
    @Field("fstDspslHm")
    private LocalTime firstAuctionTime;

    /**
     * 두 번째 경매 진행 시간 (예: null → 진행되지 않음)
     */
    @Field("scndDspslHm")
    private LocalTime secondAuctionTime;

    /**
     * 세 번째 경매 진행 시간 (예: null → 진행되지 않음)
     */
    @Field("thrdDspslHm")
    private LocalTime thirdAuctionTime;

    /**
     * 네 번째 경매 진행 시간 (예: null → 진행되지 않음)
     */
    @Field("fothDspslHm")
    private LocalTime fourthAuctionTime;

    /**
     * 경매 결정 날짜 (예: "2025-03-06")
     */
    @Field("dspslDcsnDxdyYmd")
    private LocalDate auctionDecisionDate;

    /**
     * 경매 물건 문서 ID (예: "C60DF48BAC1906DA775CF92C01211EE7")
     */
    @Field("dspslGdsSpcfcEcdocId")
    private String auctionDocumentId;

    /**
     * 경매 진행 상태 코드 (예: "00")
     */
    @Field("auctnDxdyGdsStatCd")
    private String auctionExecutionStatusCode;

    /**
     * 경매 진행 장소 (예: "경매법정(4별관 211호)")
     */
    @Field("dspslPlcNm")
    private String auctionPlace;

    /**
     * 경매 결정 시간 (예: "14:00")
     */
    @Field("dspslDcsnHm")
    private String auctionDecisionTime;

    /**
     * 경매 결정 장소 (예: "3층 7호 법정(4별관)")
     */
    @Field("dspslDcsnPlcNm")
    private String auctionDecisionPlace;

    /**
     * 입찰 시작일 (예: null → 진행되지 않음)
     */
    @Field("bidBgngYmd")
    private LocalDate bidStartDate;

    /**
     * 입찰 종료일 (예: null → 진행되지 않음)
     */
    @Field("bidEndYmd")
    private LocalDate bidEndDate;

    /**
     * 법원 이름 (예: "서울중앙지방법원")
     */
    @Field("cortOfcNm")
    private String courtName;

    /**
     * 지원 법원 이름 (예: "서울중앙지방법원")
     */
    @Field("cortSptNm")
    private String supportCourtName;

    /**
     * 실제 물건 유형 (예: "1")
     */
    @Field("realMulKind")
    private String realEstateType;

    /**
     * 인가 코드 (예: "YQN2WHHG1nS29x92QX9H4LLjRRKBbUrj7vn1CXtTlONucV8ZANXWA8KIIxcTe6ott+ieyV…")
     */
    @Field("orvParam")
    private String approvalCode;
}
