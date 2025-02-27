package apt.auctionapi.service;

import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse;
import apt.auctionapi.entity.Interest;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.AuctionSummary;
import apt.auctionapi.repository.AuctionRepository;
import apt.auctionapi.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 경매 데이터 서비스 (Auction Service)
 * <p>
 * 이 클래스는 경매 데이터 조회 및 관련 비즈니스 로직을 담당합니다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final InterestRepository interestRepository;

    public List<AuctionSummaryGroupedResponse> getAuctionsByLocationRange(
            double lbLat,
            double lbLng,
            double rtLat,
            double rtLng
    ) {
        // DB에서 좌표 범위 내의 경매 데이터를 조회
        List<AuctionSummary> auctionSummaries = auctionRepository.findByLocationRange(lbLat, lbLng, rtLat, rtLng);

        // 같은 좌표(lat, lng)를 기준으로 그룹화
        Map<String, List<InnerAuctionSummaryResponse>> groupedAuctions = auctionSummaries.stream()
                .filter(auction -> auction.getAuctionObject() != null)  // Null 체크
                .collect(Collectors.groupingBy(
                        auction -> auction.getAuctionObject().getLatitude() + "," + auction.getAuctionObject().getLongitude(),
                        Collectors.mapping(InnerAuctionSummaryResponse::from, Collectors.toList())
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

    public Auction getAuctionById(String id) {
        return auctionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Auction not found"));
    }

    public List<AuctionSummary> getInterestedAuctions(Member member) {
        var auctionIds = interestRepository.findAllByMemberId(member.getId()).stream()
                .map(Interest::getAuctionId)
                .toList();

        return auctionRepository.findAllByIdIn(auctionIds);
    }

    @Transactional
    public void interestAuction(Member member, String id) {
        interestRepository.save(Interest.builder()
                .member(member)
                .auctionId(id)
                .build());
    }
}
