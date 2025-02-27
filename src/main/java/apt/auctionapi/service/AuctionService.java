package apt.auctionapi.service;

import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse;
import apt.auctionapi.entity.Interest;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tender;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.AuctionSummary;
import apt.auctionapi.repository.AuctionRepository;
import apt.auctionapi.repository.InterestRepository;
import apt.auctionapi.repository.TenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    private final TenderRepository tenderRepository;

    public List<AuctionSummaryGroupedResponse> getAuctionsByLocationRange(
            double lbLat,
            double lbLng,
            double rtLat,
            double rtLng,
            Member member
    ) {
        // DB에서 좌표 범위 내의 경매 데이터를 조회
        List<AuctionSummary> auctionSummaries = auctionRepository.findByLocationRange(lbLat, lbLng, rtLat, rtLng);
        if (member == null) {
            return getAuctionSummaryGroupedResponses(auctionSummaries, null, Collections.emptyList(), Collections.emptyList());
        }
        List<Interest> interests = interestRepository.findAllByMemberId(member.getId());
        List<Tender> tenders = tenderRepository.findAllByMemberId(member.getId());

        return getAuctionSummaryGroupedResponses(auctionSummaries, member, interests, tenders);
    }

    private List<AuctionSummaryGroupedResponse> getAuctionSummaryGroupedResponses(
            List<AuctionSummary> auctionSummaries,
            Member member,
            List<Interest> interests,
            List<Tender> tenders
    ) {
        Map<String, List<InnerAuctionSummaryResponse>> groupedAuctions = auctionSummaries.stream()
                .filter(auction -> auction.getAuctionObject() != null)  // Null 체크
                .collect(Collectors.groupingBy(
                        auction -> auction.getAuctionObject().getLatitude() + "," + auction.getAuctionObject().getLongitude(),
                        Collectors.mapping(
                                auction -> InnerAuctionSummaryResponse.of(
                                        auction,
                                        isInterestedAuction(member, auction, interests),
                                        isTenderedAuction(member, auction, tenders)
                                ),
                                Collectors.toList()
                        )
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

    private Boolean isInterestedAuction(Member member, AuctionSummary auction, List<Interest> interests) {
        if (member != null) return false;
        return interests.stream().map(Interest::getAuctionId).anyMatch(it -> it.equals(auction.getId()));
    }

    private Boolean isTenderedAuction(Member member, AuctionSummary auction, List<Tender> tenders) {
        if (member != null) return false;
        return tenders.stream().map(Tender::getAuctionId).anyMatch(it -> it.equals(auction.getId()));
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
