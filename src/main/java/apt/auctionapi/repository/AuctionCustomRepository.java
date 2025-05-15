package apt.auctionapi.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
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
        Criteria criteria = buildCriteria(filter);
        Aggregation aggregation = buildAggregation(criteria);
        AggregationResults<Auction> results = mongoTemplate.aggregate(
            aggregation, "auctions", Auction.class
        );
        return results.getMappedResults();
    }

    private Criteria buildCriteria(SearchAuctionRequest filter) {
        Criteria locationCriteria = Criteria.where("location")
            .intersects(new GeoJsonPolygon(
                new Point(filter.lbLng(), filter.lbLat()),
                new Point(filter.rtLng(), filter.lbLat()),
                new Point(filter.rtLng(), filter.rtLat()),
                new Point(filter.lbLng(), filter.rtLat()),
                new Point(filter.lbLng(), filter.lbLat())
            ));

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
            locationCriteria,
            ongoing,
            notCanceled
        );

        if (filter.minBidPrice() != null) {
            criteria.and("dspslGdsDxdyInfo.fstPbancLwsDspslPrc").gte(filter.minBidPrice());
        }

        return criteria;
    }

    private Aggregation buildAggregation(Criteria criteria) {
        ProjectionOperation project = Aggregation.project()
            .and("gdsDspslDxdyLst").as("gdsDspslDxdyLst")
            .and("id").as("id")
            .and("csBaseInfo").as("caseBaseInfo")
            .and("location").as("location")
            .and("dspslGdsDxdyInfo").as("dspslGdsDxdyInfo")
            .and("isAuctionCancelled").as("isAuctionCancelled");

        MatchOperation match = Aggregation.match(criteria);
        return Aggregation.newAggregation(project, match);
    }
}