package apt.auctionapi.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.controller.dto.request.AuctionSearchRequest;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse;
import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.Interest;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tender;
import apt.auctionapi.entity.auction.AuctionSummary;
import apt.auctionapi.repository.InterestRepository;
import apt.auctionapi.repository.TenderRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchService {

    private final InterestRepository interestRepository;
    private final TenderRepository tenderRepository;
    private final MongoTemplate mongoTemplate;
    private final InterestService interestService;
    private final AuctionService auctionService;

    public List<AuctionSummaryGroupedResponse> getAuctionsByLocationRange(
        AuctionSearchRequest filter,
        Member member
    ) {
        Criteria criteria = buildCriteria(filter);
        Aggregation aggregation = buildAggregation(criteria);
        List<AuctionSummary> auctionSummaries = executeAggregation(aggregation);

        // 유찰 횟수 필터 적용
        if (filter.failedBidCount() != null && filter.failedBidCount() > 0) {
            auctionSummaries = filterByRuptureCount(auctionSummaries, filter.failedBidCount());
        }

        // 투자 유형 태그 필터 적용
        if (filter.investmentTags() != null && !filter.investmentTags().isEmpty()) {
            auctionSummaries = filterByInvestmentTags(auctionSummaries, filter.investmentTags());
        }

        if (member == null) {
            return getAuctionSummaryGroupedResponses(auctionSummaries, null, Collections.emptyList(),
                Collections.emptyList());
        }

        List<Interest> interests = interestRepository.findAllByMemberId(member.getId());
        List<Tender> tenders = tenderRepository.findAllByMemberId(member.getId());

        return getAuctionSummaryGroupedResponses(auctionSummaries, member, interests, tenders);
    }

    private List<AuctionSummary> filterByRuptureCount(List<AuctionSummary> auctionSummaries, int failedBidCount) {
        return auctionSummaries.stream()
            .filter(summary -> {
                int ruptureCount = auctionService.getRuptureCount(summary);
                return ruptureCount >= failedBidCount;
            })
            .toList();
    }

    private List<AuctionSummary> filterByInvestmentTags(List<AuctionSummary> auctionSummaries, List<String> tagNames) {
        return auctionSummaries.stream()
            .filter(summary -> {
                List<InvestmentTag> auctionTags = auctionService.getInvestmentTags(summary);
                return tagNames.stream().anyMatch(name ->
                    auctionTags.stream().anyMatch(tag -> tag.getName().equals(name)));
            })
            .toList();
    }

    private Criteria buildCriteria(AuctionSearchRequest filter) {
        Criteria locationCriteria = where("location")
            .intersects(new GeoJsonPolygon(
                new Point(filter.lbLng(), filter.lbLat()),
                new Point(filter.rtLng(), filter.lbLat()),
                new Point(filter.rtLng(), filter.rtLat()),
                new Point(filter.lbLng(), filter.rtLat()),
                new Point(filter.lbLng(), filter.lbLat())
            ));

        Criteria ongoingAuctionCriteria = where("gdsDspslDxdyLst")
            .elemMatch(
                where("auctnDxdyKndCd").is("01")
                    .and("auctnDxdyRsltCd").is(null)
            );

        Criteria notCancelledCriteria = new Criteria().orOperator(
            where("isAuctionCancelled").is(false),
            where("isAuctionCancelled").exists(false)
        );

        Criteria criteria = new Criteria().andOperator(
            locationCriteria,
            ongoingAuctionCriteria,
            notCancelledCriteria
        );

        if (filter.minBidPrice() != null) {
            criteria.and("minBidPrice").gte(filter.minBidPrice());
        }

        return criteria;
    }

    private Aggregation buildAggregation(Criteria criteria) {
        ProjectionOperation projectStage = Aggregation.project()
            .and("gdsDspslDxdyLst").as("gdsDspslDxdyLst")
            .and("id").as("id")
            .and("csBaseInfo").as("caseBaseInfo")
            .and("location").as("location")
            .and("dspslGdsDxdyInfo.fstPbancLwsDspslPrc").as("minBidPrice")
            .and("isAuctionCancelled").as("isAuctionCancelled")
            .and(ArrayOperators.ArrayElemAt.arrayOf("gdsDspslObjctLst").elementAt(0))
            .as("auctionObject");

        MatchOperation matchStage = Aggregation.match(criteria);

        return Aggregation.newAggregation(
            projectStage,
            matchStage
        );
    }

    private List<AuctionSummary> executeAggregation(Aggregation aggregation) {
        AggregationResults<AuctionSummary> results = mongoTemplate.aggregate(
            aggregation, "auctions", AuctionSummary.class);
        return results.getMappedResults();
    }

    private List<AuctionSummaryGroupedResponse> getAuctionSummaryGroupedResponses(
        List<AuctionSummary> auctionSummaries,
        Member member,
        List<Interest> interests,
        List<Tender> tenders
    ) {
        Map<String, List<InnerAuctionSummaryResponse>> groupedAuctions = groupAuctionsByLocation(
            auctionSummaries, member, interests, tenders);
        return convertToGroupedResponses(groupedAuctions);
    }

    private Map<String, List<InnerAuctionSummaryResponse>> groupAuctionsByLocation(
        List<AuctionSummary> auctionSummaries,
        Member member,
        List<Interest> interests,
        List<Tender> tenders
    ) {
        return auctionSummaries.stream()
            .filter(this::hasValidLocation)
            .collect(Collectors.groupingBy(
                this::createLocationKey,
                Collectors.mapping(
                    auction -> createInnerAuctionResponse(auction, member, interests, tenders),
                    Collectors.toList()
                )
            ));
    }

    private boolean hasValidLocation(AuctionSummary auction) {
        return auction.getLocation() != null;
    }

    private String createLocationKey(AuctionSummary auction) {
        return auction.getLocation().getY() + "," + auction.getLocation().getX();
    }

    private InnerAuctionSummaryResponse createInnerAuctionResponse(
        AuctionSummary auction,
        Member member,
        List<Interest> interests,
        List<Tender> tenders
    ) {
        List<InvestmentTag> investmentTags = auctionService.getInvestmentTags(auction);
        return InnerAuctionSummaryResponse.of(
            auction,
            interestService.isInterestedAuction(member, auction, interests),
            interestService.isTenderedAuction(member, auction, tenders),
            investmentTags
        );
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
