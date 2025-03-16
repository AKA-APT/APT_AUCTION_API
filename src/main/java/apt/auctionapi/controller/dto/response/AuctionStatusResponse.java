package apt.auctionapi.controller.dto.response;

import java.time.LocalDate;

public record AuctionStatusResponse(
    String status,      // 낙찰 여부 ("유찰" 또는 "낙찰")
    LocalDate auctionDate, // 낙찰 기일
    Long auctionPrice,   // 낙찰 가격
    Integer ruptureCount   // ㅌ유찰 횟수
) {
}
