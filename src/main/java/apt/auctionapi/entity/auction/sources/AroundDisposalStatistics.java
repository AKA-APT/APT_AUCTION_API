package apt.auctionapi.entity.auction.sources;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * 주변 경매 통계 정보 (Around Disposal Statistics)
 * <p>
 * 이 클래스는 특정 사건과 관련된 최근 3개월, 6개월, 12개월 동안의 경매 통계 정보를 포함합니다.
 * 평균 감정가, 평균 낙찰가, 거래 건수 등의 데이터를 포함합니다.
 */
@Getter
public class AroundDisposalStatistics {

    /**
     * 최근 3개월 동안의 거래 건수 (예: 5건)
     */
    @Field("term3MgakCnt")
    private Integer transactions3Months;

    /**
     * 최근 6개월 동안의 거래 건수 (예: 10건)
     */
    @Field("term6MgakCnt")
    private Integer transactions6Months;

    /**
     * 최근 12개월 동안의 거래 건수 (예: 15건)
     */
    @Field("term12MgakCnt")
    private Integer transactions12Months;

    /**
     * 최근 3개월 동안의 평균 감정가 (예: 2,195,990,528원)
     */
    @Field("term3AvgGamEvalAmt")
    private BigDecimal avgAppraisedValue3Months;

    /**
     * 최근 6개월 동안의 평균 감정가 (예: 1,998,794,980원)
     */
    @Field("term6AvgGamEvalAmt")
    private BigDecimal avgAppraisedValue6Months;

    /**
     * 최근 12개월 동안의 평균 감정가 (예: 1,610,078,891원)
     */
    @Field("term12AvgGamEvalAmt")
    private BigDecimal avgAppraisedValue12Months;

    /**
     * 최근 3개월 동안의 평균 낙찰가 (예: 1,525,585,222원)
     */
    @Field("term3AvgMgakPrc")
    private BigDecimal avgWinningBidPrice3Months;

    /**
     * 최근 6개월 동안의 평균 낙찰가 (예: 1,511,024,411원)
     */
    @Field("term6AvgMgakPrc")
    private BigDecimal avgWinningBidPrice6Months;

    /**
     * 최근 12개월 동안의 평균 낙찰가 (예: 1,245,882,252원)
     */
    @Field("term12AvgMgakPrc")
    private BigDecimal avgWinningBidPrice12Months;

    /**
     * 최근 3개월 동안의 평균 층 수 (예: 2.2층)
     */
    @Field("term3AvgFlbdNcnt")
    private Integer avgFloorCount3Months;

    /**
     * 최근 6개월 동안의 평균 층 수 (예: 1.9층)
     */
    @Field("term6AvgFlbdNcnt")
    private Integer avgFloorCount6Months;

    /**
     * 최근 12개월 동안의 평균 층 수 (예: 2.1층)
     */
    @Field("term12AvgFlbdNcnt")
    private Integer avgFloorCount12Months;
}
