package apt.auctionapi.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import apt.auctionapi.controller.dto.request.SearchAuctionRequest;
import apt.auctionapi.entity.auction.Auction;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuctionCustomRepository {

    private final MongoTemplate mongoTemplate;
    private static final double EARTH_RADIUS_M = 6378137.0; // 지구 반지름 (meter)

    public List<Auction> findByLocationRange(SearchAuctionRequest filter) {
        // 1) MongoDB 로 원형(반경) 조회
        Criteria criteria = buildCriteria(filter);
        Aggregation aggregation = buildAggregation(criteria);
        AggregationResults<Auction> results = mongoTemplate.aggregate(
            aggregation, "auctions", Auction.class
        );
        List<Auction> candidates = results.getMappedResults();

        // // 2) 코드 레벨에서 사각형(bounds) 필터링
        // double lbLat = filter.lbLat();
        // double rtLat = filter.rtLat();
        // double lbLng = filter.lbLng();
        // double rtLng = filter.rtLng();
        //
        // return candidates.stream()
        //     .filter(a -> {
        //         // GeoJSON Point: coordinates = [lng, lat]
        //         List<Double> coords = a.getLocation().getCoordinates();
        //         double lng = coords.get(0);
        //         double lat = coords.get(1);
        //         return lat >= lbLat && lat <= rtLat
        //             && lng >= lbLng && lng <= rtLng;
        //     })
        //     .collect(Collectors.toList());
        return candidates;
    }

    private Criteria buildCriteria(SearchAuctionRequest filter) {
        // 중심 좌표 계산
        double centerLat = (filter.lbLat() + filter.rtLat()) / 2;
        double centerLng = (filter.lbLng() + filter.rtLng()) / 2;

        // 단순 유클리디안 거리 → 근사 반경(meter) 계산
        double latDiff = filter.rtLat() - centerLat;
        double lngDiff = filter.rtLng() - centerLng;
        double approxRadiusMeter = Math.sqrt(latDiff * latDiff + lngDiff * lngDiff) * 111_000; // 1도 ≈ 111km

        double radiusInRadians = approxRadiusMeter / EARTH_RADIUS_M;

        Criteria locationCriteria = Criteria.where("location")
            .withinSphere(new Circle(new Point(centerLng, centerLat), radiusInRadians));

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Criteria ongoing = Criteria.where("gdsDspslDxdyLst")
            .elemMatch(Criteria.where("auctnDxdyKndCd").is("01")
                .and("auctnDxdyRsltCd").is(null)
            )
            .and("dspslGdsDxdyInfo.dspslDxdyYmd").gt(today);

        Criteria notCanceled = new Criteria().orOperator(
            Criteria.where("isAuctionCancelled").is(false),
            Criteria.where("isAuctionCancelled").exists(false)
        );

        Criteria criteria = new Criteria().andOperator(
            locationCriteria
            // ongoing,
            // notCanceled
        );

        if (filter.minBidPrice() != null) {
            criteria.and("dspslGdsDxdyInfo.fstPbancLwsDspslPrc").gte(filter.minBidPrice());
        }

        return criteria;
    }

    private Aggregation buildAggregation(Criteria criteria) {
        MatchOperation match = Aggregation.match(criteria);

        ProjectionOperation project = Aggregation.project()
            .and("gdsDspslDxdyLst").as("gdsDspslDxdyLst")
            .and("id").as("id")
            .and("csBaseInfo").as("csBaseInfo")
            .and("location").as("location")
            .and("dspslGdsDxdyInfo").as("dspslGdsDxdyInfo")
            .and("isAuctionCancelled").as("isAuctionCancelled")
            .and("gdsDspslObjctLst").as("gdsDspslObjctLst")
            .and("aeeWevlMnpntLst").as("aeeWevlMnpntLst");

        return Aggregation.newAggregation(project, match);
    }
}
