package apt.auctionapi.controller.dto.response;

public record LocationResponse(
    String sido,        // 시도
    String sigungu,     // 시군구
    String dong,        // 읍면동
    String fullAddress  // 전체 주소
) {

} 
