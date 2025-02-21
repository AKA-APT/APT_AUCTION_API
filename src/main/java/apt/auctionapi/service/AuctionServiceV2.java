package apt.auctionapi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse;
import apt.auctionapi.entity.AuctionSummary;
import apt.auctionapi.repository.AuctionRepositoryV2;
import lombok.RequiredArgsConstructor;

/**
 * 경매 데이터 서비스 (Auction Service)
 * <p>
 * 이 클래스는 경매 데이터 조회 및 관련 비즈니스 로직을 담당합니다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionServiceV2 {

    private final AuctionRepositoryV2 auctionRepository;

    public List<AuctionSummaryGroupedResponse> getAuctionsByLocationRange(double lbLat, double lbLng, double rtLat,
        double rtLng) {
        // DB에서 좌표 범위 내의 경매 데이터를 조회
        List<AuctionSummary> auctionSummaries = auctionRepository.findByLocationRange(lbLat, lbLng, rtLat, rtLng);

        // 같은 좌표(lat, lng)를 기준으로 그룹화
        Map<String, List<InnerAuctionSummaryResponse>> groupedAuctions = auctionSummaries.stream()
            .filter(auction -> auction.getAuctionObject() != null)  // Null 체크
            .collect(Collectors.groupingBy(
                auction -> auction.getAuctionObject().getLatitude() + "," + auction.getAuctionObject().getLongitude(),
                Collectors.mapping(this::convertToResponse, Collectors.toList())
            ));

        // DTO 변환 (좌표 기준으로 그룹화된 데이터)
        return groupedAuctions.entrySet().stream()
            .map(entry -> {
                String[] coordinates = entry.getKey().split(",");
                double latitude = Double.parseDouble(coordinates[0]);
                double longitude = Double.parseDouble(coordinates[1]);

                return AuctionSummaryGroupedResponse.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .totalCount(entry.getValue().size())  // 해당 좌표의 경매 데이터 개수
                    .auctions(entry.getValue())  // 해당 좌표에 속한 경매 리스트
                    .build();
            })
            .collect(Collectors.toList());
    }

    /**
     * AuctionSummary 엔티티를 DTO로 변환하는 메서드
     */
    private InnerAuctionSummaryResponse convertToResponse(AuctionSummary auctionSummary) {
        return InnerAuctionSummaryResponse.builder()
            .id(auctionSummary.getId())
            .caseBaseInfo(auctionSummary.getCaseBaseInfo())
            .auctionObject(auctionSummary.getAuctionObject())
            .build();
    }
}
