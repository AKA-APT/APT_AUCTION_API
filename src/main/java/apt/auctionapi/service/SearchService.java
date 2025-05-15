package apt.auctionapi.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
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
        SearchAuctionRequest filter,
        Member member
    ) {
        Criteria criteria = buildCriteria(filter);
        Aggregation aggregation = buildAggregation(criteria);
        List<Auction> auctions = executeAggregation(aggregation);
        auctions.forEach(Auction::mappingCodeValues);

        // 유찰 횟수 필터 적용
        if (filter.failedBidCount() != null && filter.failedBidCount() > 0) {
            auctions = filterByRuptureCount(auctions, filter.failedBidCount());
        }

        // 투자 유형 태그 필터 적용
        if (filter.investmentTags() != null && !filter.investmentTags().isEmpty()) {
            auctions = filterByInvestmentTags(auctions, filter.investmentTags());
        }

        if (member == null) {
            return getAuctionSummaryGroupedResponses(auctions, null, Collections.emptyList(),
                Collections.emptyList());
        }

        List<Interest> interests = interestRepository.findAllByMemberId(member.getId());
        List<Tender> tenders = tenderRepository.findAllByMemberId(member.getId());

        return getAuctionSummaryGroupedResponses(auctions, member, interests, tenders);
    }

    private List<Auction> filterByRuptureCount(List<Auction> auctions, int failedBidCount) {
        return auctions.stream()
            .filter(auction -> {
                int ruptureCount = auctionService.getRuptureCount(auction);
                return ruptureCount >= failedBidCount;
            })
            .toList();
    }

    private List<Auction> filterByInvestmentTags(List<Auction> auctions, List<String> tagNames) {
        return auctions.stream()
            .filter(auction -> {
                List<InvestmentTag> auctionTags = auctionService.getInvestmentTags(auction);
                return tagNames.stream().anyMatch(name ->
                    auctionTags.stream().anyMatch(tag -> tag.getName().equals(name)));
            })
            .toList();
    }

    private Criteria buildCriteria(SearchAuctionRequest filter) {
        Criteria locationCriteria = where("location")
            .intersects(new GeoJsonPolygon(
                new Point(filter.lbLng(), filter.lbLat()),
                new Point(filter.rtLng(), filter.lbLat()),
                new Point(filter.rtLng(), filter.rtLat()),
                new Point(filter.lbLng(), filter.rtLat()),
                new Point(filter.lbLng(), filter.lbLat())
            ));

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Criteria ongoingAuctionCriteria = where("gdsDspslDxdyLst")
            .elemMatch(
                where("auctnDxdyKndCd").is("01")
                    .and("auctnDxdyRsltCd").is(null)
            ).and("dspslGdsDxdyInfo.dspslDxdyYmd").gt(today);

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
            criteria.and("dspslGdsDxdyInfo.fstPbancLwsDspslPrc").gte(filter.minBidPrice());
        }

        return criteria;
    }

    private Aggregation buildAggregation(Criteria criteria) {
        ProjectionOperation projectStage = Aggregation.project()
            .and("gdsDspslDxdyLst").as("gdsDspslDxdyLst")
            .and("id").as("id")
            .and("csBaseInfo").as("caseBaseInfo")
            .and("location").as("location")
            .and("dspslGdsDxdyInfo").as("dspslGdsDxdyInfo")
            .and("isAuctionCancelled").as("isAuctionCancelled");

        MatchOperation matchStage = Aggregation.match(criteria);

        return Aggregation.newAggregation(
            projectStage,
            matchStage
        );
    }

    private List<Auction> executeAggregation(Aggregation aggregation) {
        AggregationResults<Auction> results = mongoTemplate.aggregate(
            aggregation, "auctions", Auction.class);
        return results.getMappedResults();
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
        List<InvestmentTag> investmentTags = auctionService.getInvestmentTags(auction);
        return InnerAuctionSummaryResponse.of(
            auction,
            interestService.isInterestedAuctionByAuction(member, auction, interests),
            interestService.isTenderedAuctionByAuction(member, auction, tenders),
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
