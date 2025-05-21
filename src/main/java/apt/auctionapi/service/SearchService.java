package apt.auctionapi.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.controller.dto.request.SearchAuctionRequest;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
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
            .filter(inner -> {
                boolean isInProgress = !inner.auctionStatus().status().equals("낙찰");
                if (filter.isInProgress()) {
                    return isInProgress;
                } else {
                    return !isInProgress;
                }
            })
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

    private boolean hasValidLocation(Auction auction) {
        return auction.getLocation() != null;
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
}
