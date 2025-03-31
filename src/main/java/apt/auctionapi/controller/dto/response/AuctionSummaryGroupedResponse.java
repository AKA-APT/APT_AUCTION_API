package apt.auctionapi.controller.dto.response;

import java.util.List;

import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.auction.Auction;
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
        InnerAuctionObject auctionObject,
        AuctionStatusResponse auctionStatus,
        boolean isInterested,  // 사용자가 좋아요한 경매인지 여부
        boolean isBidding, // 사용자가 입찰한 물건인지 여부
        List<InvestmentTagResponse> investmentTags // 투자 유형 태그 목록
    ) {

        public static InnerAuctionSummaryResponse of(
            Auction auction,
            boolean isInterested,
            boolean isBidding,
            List<InvestmentTag> investmentTags
        ) {
            return InnerAuctionSummaryResponse.builder()
                .id(auction.getId())
                .isInterested(isInterested)
                .isBidding(isBidding)
                .investmentTags(investmentTags.stream()
                    .map(tag -> new InvestmentTagResponse(tag.getId(), tag.getName(), tag.getDescription()))
                    .toList())
                .auctionStatus(AuctionStatusResponse.from(auction))
                .auctionObject(InnerAuctionObject.from(auction))
                .build();
        }

        public record InnerAuctionObject(
            Double latitude,
            Double longitude
        ) {
            public static InnerAuctionObject from(Auction auction) {
                return new InnerAuctionObject(auction.getLocation().getY(), auction.getLocation().getX());
            }
        }
    }

    @Builder
    public record InvestmentTagResponse(
        int id,        // 태그 ID
        String name,   // 태그 이름
        String description // 태그 설명
    ) {
    }
}
