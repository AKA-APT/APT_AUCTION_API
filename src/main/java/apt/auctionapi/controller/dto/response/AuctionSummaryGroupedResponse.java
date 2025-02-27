package apt.auctionapi.controller.dto.response;

import java.util.List;

import apt.auctionapi.entity.auction.AuctionSummary;
import apt.auctionapi.entity.auction.sources.AuctionObject;
import apt.auctionapi.entity.auction.sources.CaseBaseInfo;
import lombok.Builder;

@Builder
public record AuctionSummaryGroupedResponse(
    double latitude,  // 그룹화된 위도
    double longitude, // 그룹화된 경도
    int totalCount,  // 해당 좌표에 존재하는 경매 데이터 총 개수
    List<InnerAuctionSummaryResponse> auctions  // 해당 좌표의 경매 데이터 리스트
) {

    @Builder
    public record InnerAuctionSummaryResponse(
        String id,  // 문서 ID
        CaseBaseInfo caseBaseInfo,  // 사건 기본 정보
        AuctionObject auctionObject,  // 경매 대상 물건 정보
        boolean isInterested,  // 사용자가 좋아요한 경매인지 여부
        boolean isBidding // 사용자가 입찰한 물건인지 여부
    ) {
        public static InnerAuctionSummaryResponse of(
                AuctionSummary auctionSummary,
                boolean isInterested,
                boolean isBidding
        ) {
            return InnerAuctionSummaryResponse.builder()
                    .id(auctionSummary.getId())
                    .caseBaseInfo(auctionSummary.getCaseBaseInfo())
                    .auctionObject(auctionSummary.getAuctionObject())
                    .isInterested(isInterested)
                    .isBidding(isBidding)
                    .build();
        }
    }
}
