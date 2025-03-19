package apt.auctionapi.service;

import apt.auctionapi.controller.dto.request.AuctionSearchRequest;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse.InnerAuctionSummaryResponse;
import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.Interest;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tender;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.AuctionSummary;
import apt.auctionapi.repository.AuctionRepository;
import apt.auctionapi.repository.InterestRepository;
import apt.auctionapi.repository.TenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final MongoTemplate mongoTemplate;

    public List<AuctionSummaryGroupedResponse> getAuctionsByLocationRange(
            AuctionSearchRequest filter,
            Member member
    ) {
        Criteria criteria = buildCriteria(filter);
        Aggregation aggregation = buildAggregation(criteria);
        List<AuctionSummary> auctionSummaries = executeAggregation(aggregation);

        // 유찰 횟수 필터 적용 (Java 코드에서)
        if (filter.failedBidCount() != null && filter.failedBidCount() > 0) {
            auctionSummaries = filterByRuptureCount(auctionSummaries, filter.failedBidCount());
        }

        // 투자 유형 태그 필터 적용 (Java 코드에서)
        if (filter.investmentTags() != null && !filter.investmentTags().isEmpty()) {
            auctionSummaries = filterByInvestmentTags(auctionSummaries, filter.investmentTags());
        }

        if (member == null) {
            return getAuctionSummaryGroupedResponses(auctionSummaries, null, Collections.emptyList(), Collections.emptyList());
        }

        List<Interest> interests = interestRepository.findAllByMemberId(member.getId());
        List<Tender> tenders = tenderRepository.findAllByMemberId(member.getId());

        return getAuctionSummaryGroupedResponses(auctionSummaries, member, interests, tenders);
    }

    private List<AuctionSummary> filterByRuptureCount(List<AuctionSummary> auctionSummaries, int failedBidCount) {
        return auctionSummaries.stream()
                .filter(summary -> {
                    int ruptureCount = getRuptureCount(summary);
                    if (failedBidCount >= 6) {
                        return ruptureCount >= 5; // 5회 이상 유찰
                    } else {
                        return ruptureCount == failedBidCount; // 특정 유찰 횟수
                    }
                })
                .toList();
    }

    private List<AuctionSummary> filterByInvestmentTags(List<AuctionSummary> auctionSummaries, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return auctionSummaries;
        }

        // 태그 이름을 InvestmentTag Enum으로 변환
        List<InvestmentTag> requestedTags = tagNames.stream()
                .map(name -> Arrays.stream(InvestmentTag.values())
                        .filter(tag -> tag.getName().equals(name))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        if (requestedTags.isEmpty()) {
            return auctionSummaries;
        }

        return auctionSummaries.stream()
                .filter(summary -> {
                    // AuctionSummary에서 Auction 객체를 가져옴
                    Auction auction = auctionRepository.findById(summary.getId()).orElse(null);
                    if (auction == null) {
                        return false;
                    }

                    // Auction 객체로부터 투자 유형 태그 목록을 가져옴
                    List<InvestmentTag> auctionTags = InvestmentTag.from(auction);

                    // 요청된 태그 중 하나라도 포함되어 있으면 필터링 통과
                    return requestedTags.stream().anyMatch(auctionTags::contains);
                })
                .toList();
    }

    private Criteria buildCriteria(AuctionSearchRequest filter) {
        Criteria locationCriteria = Criteria.where("loc")
                .intersects(new GeoJsonPolygon(
                        new Point(filter.lbLng(), filter.lbLat()),  // 좌하단
                        new Point(filter.rtLng(), filter.lbLat()),  // 우하단
                        new Point(filter.rtLng(), filter.rtLat()),  // 우상단
                        new Point(filter.lbLng(), filter.rtLat()),  // 좌상단
                        new Point(filter.lbLng(), filter.lbLat())   // 다각형 닫기
                ));

        Criteria criteria = new Criteria().andOperator(locationCriteria);

        // 최소낙찰가 필터 적용
        if (filter.minBidPrice() != null) {
            criteria.and("auctionObject.aeeEvlAmt").gte(filter.minBidPrice());
        }

        return criteria;
    }

    private Aggregation buildAggregation(Criteria criteria) {
        ProjectionOperation projectStage = Aggregation.project()
                .and("id").as("id")
                .and("csBaseInfo").as("caseBaseInfo")
                .and("loc").as("loc")
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
                aggregation, "detail_auctions", AuctionSummary.class);

        return results.getMappedResults();
    }

    // AuctionSummary에서 유찰 횟수를 계산하는 메서드
    private int getRuptureCount(AuctionSummary auctionSummary) {
        // AuctionSummary에서 Auction 객체를 가져오는 방법에 따라 수정 필요
        Auction auction = auctionRepository.findById(auctionSummary.getId())
                .orElse(null);
        return getRuptureCount(auction);
    }

    // 기존 유찰 횟수 계산 메서드
    private Integer getRuptureCount(Auction auction) {
        if (auction == null || auction.getAuctionScheduleList() == null) {
            return 0; // auction 또는 일정 리스트가 없으면 0 반환
        }

        return (int) auction.getAuctionScheduleList().stream()
                .filter(schedule -> "002".equals(schedule.getAuctionResultCode())) // 유찰 코드 필터링
                .count();
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
                                auction -> {
                                    // Auction 객체에서 투자 유형 태그 목록 가져오기
                                    List<InvestmentTag> investmentTags = getInvestmentTags(auction);

                                    return InnerAuctionSummaryResponse.of(
                                            auction,
                                            isInterestedAuction(member, auction, interests),
                                            isTenderedAuction(member, auction, tenders),
                                            investmentTags
                                    );
                                },
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
                .toList();
    }

    // AuctionSummary에서 투자 유형 태그 목록을 가져오는 메서드
    private List<InvestmentTag> getInvestmentTags(AuctionSummary auctionSummary) {
        Auction auction = auctionRepository.findById(auctionSummary.getId()).orElse(null);
        if (auction == null) {
            return Collections.emptyList();
        }
        return InvestmentTag.from(auction);
    }

    private Boolean isInterestedAuction(Member member, AuctionSummary auction, List<Interest> interests) {
        if (member == null) return false;
        return interests.stream().map(Interest::getAuctionId).anyMatch(it -> it.equals(auction.getId()));
    }

    private Boolean isTenderedAuction(Member member, AuctionSummary auction, List<Tender> tenders) {
        if (member == null) return false;
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
        Optional<Interest> existingInterest = interestRepository.findByMemberAndAuctionId(member, id);

        if (existingInterest.isPresent()) {
            interestRepository.delete(existingInterest.get()); // 이미 존재하면 삭제
        } else {
            interestRepository.save(Interest.builder()
                    .member(member)
                    .auctionId(id)
                    .build()); // 존재하지 않으면 추가
        }
    }

    /**
     * 경매 객체의 투자 유형 태그 목록을 반환합니다.
     *
     * @param auctionId 경매 ID
     * @return 투자 유형 태그 목록
     */
    public List<InvestmentTag> getInvestmentTagsForAuction(String auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        return InvestmentTag.from(auction);
    }
}
