package apt.auctionapi.controller.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.sources.AuctionSchedule;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경매 상태 상세 정보")
public record AuctionStatusResponse(
    @Schema(description = "경매 상태", example = "매각",
        allowableValues = {"유찰", "매각", "변경", "취소", "진행"})
    String status,          // 경매 상태 ("유찰", "매각", "변경", "취소", "진행" 등)

    @Schema(description = "낙찰 기일", example = "2025-03-15")
    LocalDate auctionDate,  // 낙찰 기일

    @Schema(description = "낙찰 가격", example = "150000000")
    BigDecimal auctionPrice, // 낙찰 가격

    @Schema(description = "유찰 횟수", example = "2")
    Long ruptureCount       // 유찰 횟수
) {
    // 경매 결과 코드 상수
    private static final String AUCTION_FAILURE_CODE = "002";  // 유찰
    private static final String AUCTION_SALE_CODE = "001";     // 매각
    private static final String AUCTION_CHANGE_CODE = "014";   // 변경
    private static final String DEFAULT_STATUS = "진행";       // 기본 상태 (결과코드 없을 때)

    @Schema(hidden = true)
    public static AuctionStatusResponse from(Auction auction) {
        // 경매가 취소된 경우 우선 처리
        if (Boolean.TRUE.equals(auction.getIsAuctionCancelled())) {
            return new AuctionStatusResponse(
                "취소",
                auction.getDisposalGoodsExecutionInfo().getAuctionDate(),
                BigDecimal.ZERO,
                calculateFailureCount(auction)
            );
        }

        LocalDate targetDate = auction.getDisposalGoodsExecutionInfo().getAuctionDate();

        // 해당 날짜의 경매 일정 찾기 (한 번만 스트림 순회)
        Optional<AuctionSchedule> targetSchedule = auction.getAuctionScheduleList().stream()
            .filter(schedule -> schedule.getAuctionDate().equals(targetDate))
            .findFirst();

        // 결과 코드와 최종 가격 가져오기
        String resultCode = targetSchedule
            .map(AuctionSchedule::getAuctionResultCode)
            .orElse(null);

        BigDecimal finalPrice = targetSchedule
            .map(AuctionSchedule::getFinalAuctionPrice)
            .orElse(BigDecimal.ZERO);

        // 결과 코드에 따른 상태 결정
        String status = mapCodeToStatus(resultCode);

        return new AuctionStatusResponse(
            status,
            targetDate,
            finalPrice,
            calculateFailureCount(auction)
        );
    }

    // 결과 코드를 상태 텍스트로 변환하는 메서드
    @Schema(hidden = true)
    private static String mapCodeToStatus(String resultCode) {
        // 결과 코드가 null이거나 빈 문자열인 경우 "진행" 반환
        if (resultCode == null || resultCode.isEmpty()) {
            return DEFAULT_STATUS;
        }

        return switch (resultCode) {
            case AUCTION_FAILURE_CODE -> "유찰";
            case AUCTION_SALE_CODE -> "매각";
            case AUCTION_CHANGE_CODE -> "변경";
            default -> resultCode; // 기본값은 코드 그대로 반환
        };
    }

    // 유찰 횟수 계산 메서드
    @Schema(hidden = true)
    private static Long calculateFailureCount(Auction auction) {
        return auction.getAuctionScheduleList().stream()
            .filter(schedule -> AUCTION_FAILURE_CODE.equals(schedule.getAuctionResultCode()))
            .count();
    }
}