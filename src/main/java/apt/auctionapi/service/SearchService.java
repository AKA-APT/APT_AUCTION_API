package apt.auctionapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.controller.dto.request.SearchAuctionLocationsRequest;
import apt.auctionapi.controller.dto.request.SearchAuctionRequest;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.controller.dto.response.SearchAuctionLocationsResponse;
import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.Interest;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tender;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.AuctionLocation;
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
    private final TagService tagService;

    public List<AuctionSummaryGroupedResponse> getAuctionsByLocationRange(
        SearchAuctionRequest filter,
        Member member
    ) {
        // 1) 필터링된 Auction 조회
        List<Auction> auctions = auctionCustomRepository.findByLocationRange(filter);
        for (Auction auction : auctions) {
            auction.mappingCodeValues();
        }

        // 2) 관심 및 입찰 목록 조회
        List<Interest> interests = member == null
            ? Collections.emptyList()
            : interestRepository.findAllByMemberId(member.getId());
        List<Tender> tenders = member == null
            ? Collections.emptyList()
            : tenderRepository.findAllByMemberId(member.getId());
        List<InvestmentTag> userTags = member == null
            ? List.of()
            : tagService.getInvestmentTagsForMember(member);

        // 3) Inner DTO 생성
        List<AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse> innerList =
            new ArrayList<>();
        for (Auction auction1 : auctions) {
            if (hasValidLocation(auction1)) {
                List<InvestmentTag> matchedTags = getInvestmentTags(auction1).stream()
                    .filter(userTags::contains)
                    .toList();

                AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse applied = AuctionSummaryGroupedResponse
                    .InnerAuctionSummaryResponse.of(
                        auction1,
                        isInterestedAuctionByAuction(member, auction1, interests),
                        isTenderedAuctionByAuction(member, auction1, tenders),
                        matchedTags
                    );
                innerList.add(applied);
            }
        }

        // 4) DTO 리스트 변환 (중복 제거 없이 그대로 매핑)
        List<AuctionSummaryGroupedResponse> list = new ArrayList<>();
        for (AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse innerAuctionSummaryResponse : innerList) {
            double lat = innerAuctionSummaryResponse.auctionObject().latitude();
            double lng = innerAuctionSummaryResponse.auctionObject().longitude();
            AuctionSummaryGroupedResponse apply = AuctionSummaryGroupedResponse.builder()
                .latitude(lat)
                .longitude(lng)
                .totalCount(1)
                .auctions(List.of(innerAuctionSummaryResponse))
                .build();
            list.add(apply);
        }
        return list;
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
        for (Interest interest : interests) {
            String id = interest.getAuctionId();
            if (id.equals(auction.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean isTenderedAuctionByAuction(Member member, Auction auction, List<Tender> tenders) {
        if (member == null || auction == null) {
            return false;
        }
        for (Tender tender : tenders) {
            String id = tender.getAuctionId();
            if (id.equals(auction.getId())) {
                return true;
            }
        }
        return false;
    }

    public SearchAuctionLocationsResponse getLightAuctionsByLocationRange(
        SearchAuctionLocationsRequest filter
    ) {
        List<AuctionLocation> locations = auctionCustomRepository.findLightweightByLocationRange(filter);
        return SearchAuctionLocationsResponse.from(locations);
    }
}
