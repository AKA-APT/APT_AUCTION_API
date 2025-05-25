package apt.auctionapi.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.sources.AuctionSchedule;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경매 상태 상세 정보")
public record AuctionStatusResponse(
    @Schema(description = "경매 상태", example = "매각",
        allowableValues = {"매각", "진행"})
    String status,          // 경매 상태 ("매각", 진행")

    @Schema(description = "낙찰 기일", example = "2025-03-15")
    LocalDate auctionDate,  // 낙찰 기일

    @Schema(description = "매각가격", example = "150000000")
    BigDecimal auctionPrice, // 매각가격

    @Schema(description = "최저낙찰가", example = "160000000")
    BigDecimal minimumPrice, // 최저낙찰가

    @Schema(description = "감정평가액", example = "200000000")
    BigDecimal appraisedValue, // 감정평가액

    @Schema(description = "유찰 횟수", example = "2")
    Long ruptureCount,      // 유찰 횟수

    @Schema(description = "매물 용도", example = "주거")
    String propertyUsage    // 매물 용도
) {
    // 경매 결과 코드 상수
    private static final String AUCTION_FAILURE_CODE = "002";  // 유찰

    @Schema(hidden = true)
    public static AuctionStatusResponse from(Auction auction) {

        LocalDate targetDate = auction.getDisposalGoodsExecutionInfo().getAuctionDate();

        // 해당 날짜의 경매 일정 찾기 (한 번만 스트림 순회)
        Optional<AuctionSchedule> targetSchedule = Optional.empty();
        for (AuctionSchedule schedule : auction.getAuctionScheduleList()) {
            if (Objects.equals(schedule.getAuctionDate(), targetDate)) {
                targetSchedule = Optional.of(schedule);
                break;
            }
        }

        BigDecimal finalPrice = targetSchedule
            .map(AuctionSchedule::getFinalAuctionPrice)
            .orElse(BigDecimal.ZERO);

        // 최저낙찰가 가져오기 (최초 경매 시작 가격 사용)
        BigDecimal minimumPrice = auction.getDisposalGoodsExecutionInfo().getFirstAuctionPrice();

        // 감정평가액 가져오기
        BigDecimal appraisedValue = auction.getDisposalGoodsExecutionInfo().getAppraisedValue();

        String status = auction.getAuctionStatus();

        return new AuctionStatusResponse(
            status,
            targetDate,
            finalPrice,
            minimumPrice,
            appraisedValue,
            calculateFailureCount(auction),
            auction.getDisposalGoodsExecutionInfo().getAuctionGoodsUsage()
        );
    }

    // 유찰 횟수 계산 메서드
    @Schema(hidden = true)
    private static Long calculateFailureCount(Auction auction) {
        long count = 0L;
        for (AuctionSchedule schedule : auction.getAuctionScheduleList()) {
            if (AUCTION_FAILURE_CODE.equals(schedule.getAuctionResultCode())) {
                count++;
            }
        }
        return count;
    }
}
