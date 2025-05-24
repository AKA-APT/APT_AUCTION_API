package apt.auctionapi.repository;

import java.util.List;

import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
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

    public List<Auction> findByLocationRange(SearchAuctionRequest filter) {
        // 1) MongoDB 로 박스(사각형) 조회
        Criteria criteria = buildBoxCriteria(filter);
        Aggregation aggregation = buildAggregation(criteria);
        AggregationResults<Auction> results = mongoTemplate.aggregate(
            aggregation, "auctions", Auction.class
        );

        // 2) (필요 없으면 제거) 추가 필터링 로직이 없다면 바로 반환
        return results.getMappedResults();
    }

    private Criteria buildBoxCriteria(SearchAuctionRequest filter) {
        // 좌하단, 좌상단, 우상단, 우하단, 다시 좌하단 순서로 폴리곤 닫기
        Point lowerLeft = new Point(filter.lbLng(), filter.lbLat());
        Point upperLeft = new Point(filter.lbLng(), filter.rtLat());
        Point upperRight = new Point(filter.rtLng(), filter.rtLat());
        Point lowerRight = new Point(filter.rtLng(), filter.lbLat());

        // 박스 폴리곤
        Polygon box = new Polygon(lowerLeft, upperLeft, upperRight, lowerRight, lowerLeft);

        // GeoJSON 필드에 대해 $geoWithin + Polygon 사용
        return Criteria.where("location")
            .within(box);
    }

    private Aggregation buildAggregation(Criteria criteria) {
        // 1) 반드시 match 먼저
        MatchOperation match = Aggregation.match(criteria);

        // 2) 그 다음에 project
        ProjectionOperation project = Aggregation.project()
            .and("gdsDspslDxdyLst").as("gdsDspslDxdyLst")
            .and("id").as("id")
            .and("csBaseInfo").as("csBaseInfo")
            .and("location").as("location")
            .and("dspslGdsDxdyInfo").as("dspslDxdyInfo")
            .and("isAuctionCancelled").as("isAuctionCancelled")
            .and("gdsDspslObjctLst").as("gdsDspslObjctLst")
            .and("aeeWevlMnpntLst").as("aeeWevlMnpntLst");

        // match → project 순으로 배열
        return Aggregation.newAggregation(
            match,
            project
        );
    }
}
