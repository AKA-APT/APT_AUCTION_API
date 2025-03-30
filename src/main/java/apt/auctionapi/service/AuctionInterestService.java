package apt.auctionapi.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse;
import apt.auctionapi.entity.Interest;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tender;
import apt.auctionapi.entity.auction.AuctionSummary;
import apt.auctionapi.repository.AuctionRepository;
import apt.auctionapi.repository.InterestRepository;
import apt.auctionapi.repository.TenderRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionInterestService {

    private final InterestRepository interestRepository;
    private final MongoTemplate mongoTemplate;

    public List<AuctionSummary> getInterestedAuctions(Member member) {
        List<String> auctionIds = interestRepository.findAllByMemberId(member.getId()).stream()
            .map(Interest::getAuctionId)
            .toList();

        if (auctionIds.isEmpty()) {
            return List.of();
        }

        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(where("_id").in(auctionIds)),
            Aggregation.project()
                .andInclude("id")
                .and("csBaseInfo").as("caseBaseInfo")  // 필드명 변경
                .and("gdsDspslObjctLst").arrayElementAt(0).as("auctionObject") // 배열 필드 변환
        );

        AggregationResults<AuctionSummary> results = mongoTemplate.aggregate(aggregation, "auctions",
            AuctionSummary.class);
        return results.getMappedResults();
    }

    @Transactional
    public void interestAuction(Member member, String id) {
        Optional<Interest> existingInterest = interestRepository.findByMemberAndAuctionId(member, id);

        if (existingInterest.isPresent()) {
            interestRepository.delete(existingInterest.get());
            return;
        }

        interestRepository.save(Interest.builder()
            .member(member)
            .auctionId(id)
            .build());
    }

    public Boolean isInterestedAuction(Member member, AuctionSummary auction, List<Interest> interests) {
        if (member == null) {
            return false;
        }
        return interests.stream()
            .map(Interest::getAuctionId)
            .anyMatch(it -> it.equals(auction.getId()));
    }

    public Boolean isTenderedAuction(Member member, AuctionSummary auction, List<Tender> tenders) {
        if (member == null) {
            return false;
        }
        return tenders.stream()
            .map(Tender::getAuctionId)
            .anyMatch(it -> it.equals(auction.getId()));
    }
}
