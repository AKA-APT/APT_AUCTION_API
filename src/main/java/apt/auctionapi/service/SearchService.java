package apt.auctionapi.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.controller.dto.request.SearchAuctionRequest;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse;
import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.Interest;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tender;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.repository.AuctionCustomRepository;
import apt.auctionapi.repository.InterestRepository;
import apt.auctionapi.repository.TenderRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchService {

    private final AuctionCustomRepository auctionCustomRepository;
    private final InterestRepository interestRepository;
    private final TenderRepository tenderRepository;

    public List<AuctionSummaryGroupedResponse> getAuctionsByLocationRange(
        SearchAuctionRequest filter,
        Member member
    ) {
        // 1) 필터링된 Auction 조회
        List<Auction> auctions = auctionCustomRepository.findByLocationRange(filter);
        auctions.forEach(Auction::mappingCodeValues);

        if (filter.failedBidCount() != null && filter.failedBidCount() > 0) {
            auctions = filterByRuptureCount(auctions, filter.failedBidCount());
        }
        if (filter.investmentTags() != null && !filter.investmentTags().isEmpty()) {
            auctions = filterByInvestmentTags(auctions, filter.investmentTags());
        }

        // 2) 관심 및 입찰 목록 조회
        List<Interest> interests = member == null
            ? Collections.emptyList()
            : interestRepository.findAllByMemberId(member.getId());
        List<Tender> tenders = member == null
            ? Collections.emptyList()
            : tenderRepository.findAllByMemberId(member.getId());

        // 3) Inner DTO 생성
        List<AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse> innerList =
            auctions.stream()
                .filter(this::hasValidLocation)
                .map(auction -> AuctionSummaryGroupedResponse
                    .InnerAuctionSummaryResponse.of(
                        auction,
                        isInterestedAuctionByAuction(member, auction, interests),
                        isTenderedAuctionByAuction(member, auction, tenders),
                        getInvestmentTags(auction)
                    )
                )
                .toList();

        // 4) 동일 좌표 첫 건만 남기기
        var uniqueByCoord = new LinkedHashMap<String, AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse>();
        innerList.forEach(inner -> {
            String key = inner.auctionObject().latitude() + "," + inner.auctionObject().longitude();
            uniqueByCoord.putIfAbsent(key, inner);
        });

        // 5) DTO 리스트 변환
        return uniqueByCoord.values().stream()
            .map(inner -> {
                double lat = inner.auctionObject().latitude();
                double lng = inner.auctionObject().longitude();
                return AuctionSummaryGroupedResponse.builder()
                    .latitude(lat)
                    .longitude(lng)
                    .totalCount(1)                      // 중복 제거했으니 항상 1
                    .auctions(List.of(inner))
                    .build();
            })
            .toList();
    }

    private List<Auction> filterByRuptureCount(List<Auction> auctions, int failedBidCount) {
        return auctions.stream()
            .filter(auction -> auction.isRupturedMoreThan(failedBidCount))
            .toList();
    }

    private List<Auction> filterByInvestmentTags(List<Auction> auctions, List<String> tagNames) {
        return auctions.stream()
            .filter(auction -> {
                List<InvestmentTag> auctionTags = getInvestmentTags(auction);
                return tagNames.stream().anyMatch(name ->
                    auctionTags.stream().anyMatch(tag -> tag.getName().equals(name)));
            })
            .toList();
    }

    private List<InvestmentTag> getInvestmentTags(Auction auction) {
        if (auction == null) {
            return List.of();
        }
        return InvestmentTag.from(auction);
    }

    private List<AuctionSummaryGroupedResponse> getAuctionSummaryGroupedResponses(
        List<Auction> auctions,
        Member member,
        List<Interest> interests,
        List<Tender> tenders
    ) {
        Map<String, List<InnerAuctionSummaryResponse>> groupedAuctions = groupAuctionsByLocation(
            auctions, member, interests, tenders);
        return convertToGroupedResponses(groupedAuctions);
    }

    private Map<String, List<InnerAuctionSummaryResponse>> groupAuctionsByLocation(
        List<Auction> auctions,
        Member member,
        List<Interest> interests,
        List<Tender> tenders
    ) {
        return auctions.stream()
            .filter(this::hasValidLocation)
            .collect(Collectors.groupingBy(
                this::createLocationKey,
                Collectors.mapping(
                    auction -> createInnerAuctionResponse(auction, member, interests, tenders),
                    Collectors.toList()
                )
            ));
    }

    private boolean hasValidLocation(Auction auction) {
        return auction.getLocation() != null;
    }

    private String createLocationKey(Auction auction) {
        return auction.getLocation().getY() + "," + auction.getLocation().getX();
    }

    private InnerAuctionSummaryResponse createInnerAuctionResponse(
        Auction auction,
        Member member,
        List<Interest> interests,
        List<Tender> tenders
    ) {
        List<InvestmentTag> investmentTags = getInvestmentTags(auction);
        return InnerAuctionSummaryResponse.of(
            auction,
            isInterestedAuctionByAuction(member, auction, interests),
            isTenderedAuctionByAuction(member, auction, tenders),
            investmentTags
        );
    }

    private boolean isInterestedAuctionByAuction(Member member, Auction auction, List<Interest> interests) {
        if (member == null || auction == null) {
            return false;
        }
        return interests.stream()
            .map(Interest::getAuctionId)
            .anyMatch(id -> id.equals(auction.getId()));
    }

    private boolean isTenderedAuctionByAuction(Member member, Auction auction, List<Tender> tenders) {
        if (member == null || auction == null) {
            return false;
        }
        return tenders.stream()
            .map(Tender::getAuctionId)
            .anyMatch(id -> id.equals(auction.getId()));
    }

    private List<AuctionSummaryGroupedResponse> convertToGroupedResponses(
        Map<String, List<InnerAuctionSummaryResponse>> groupedAuctions
    ) {
        return groupedAuctions.entrySet().stream()
            .map(this::createGroupedResponse)
            .toList();
    }

    private AuctionSummaryGroupedResponse createGroupedResponse(
        Map.Entry<String, List<InnerAuctionSummaryResponse>> entry
    ) {
        String[] coordinates = entry.getKey().split(",");
        double latitude = Double.parseDouble(coordinates[0]);
        double longitude = Double.parseDouble(coordinates[1]);

        return AuctionSummaryGroupedResponse.builder()
            .latitude(latitude)
            .longitude(longitude)
            .totalCount(entry.getValue().size())
            .auctions(entry.getValue())
            .build();
    }
}
