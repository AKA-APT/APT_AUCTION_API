package apt.auctionapi.repository;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import apt.auctionapi.controller.dto.request.SearchAuctionLocationsRequest;
import apt.auctionapi.controller.dto.request.SearchAuctionRequest;
import apt.auctionapi.entity.auction.Auction;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuctionCustomRepository {

    private final MongoTemplate mongoTemplate;

    public List<Auction> findByLocationRange(SearchAuctionRequest filter) {
        Criteria criteria = buildCriteria(filter);
        Query query = new Query(criteria);
        query.fields()
            .include("gdsDspslDxdyLst")
            .include("id")
            .include("csBaseInfo")
            .include("location")
            .include("dspslGdsDxdyInfo")
            .include("isAuctionCancelled")
            .include("gdsDspslObjctLst")
            .include("aeeWevlMnpntLst")
            .include("auctionStatus");
        return mongoTemplate.findDistinct(query, "location", Auction.class, Auction.class);
    }

    public List<GeoJsonPoint> findLightweightByLocationRange(SearchAuctionLocationsRequest filter) {
        Criteria criteria = buildCriteria(filter);
        Query query = new Query(criteria);
        query.fields()
            .include("location")
            .include("auctionStatus");
        return mongoTemplate.findDistinct(query, "location", Auction.class, GeoJsonPoint.class);
    }

    private Criteria buildCriteria(SearchAuctionRequest filter) {
        GeoJsonPoint ll = new GeoJsonPoint(filter.lbLng(), filter.lbLat());
        GeoJsonPoint ul = new GeoJsonPoint(filter.lbLng(), filter.rtLat());
        GeoJsonPoint ur = new GeoJsonPoint(filter.rtLng(), filter.rtLat());
        GeoJsonPoint lr = new GeoJsonPoint(filter.rtLng(), filter.lbLat());
        GeoJsonPolygon box = new GeoJsonPolygon(ll, ul, ur, lr, ll);
        Criteria criteria = Criteria.where("location").within(box);
        if (filter.isInProgress()) {
            criteria = criteria.and("auctionStatus").is("진행");
        } else {
            criteria = criteria.and("auctionStatus").is("낙찰");
        }
        return criteria;
    }

    private Criteria buildCriteria(SearchAuctionLocationsRequest filter) {
        GeoJsonPoint ll = new GeoJsonPoint(filter.lbLng(), filter.lbLat());
        GeoJsonPoint ul = new GeoJsonPoint(filter.lbLng(), filter.rtLat());
        GeoJsonPoint ur = new GeoJsonPoint(filter.rtLng(), filter.rtLat());
        GeoJsonPoint lr = new GeoJsonPoint(filter.rtLng(), filter.lbLat());
        GeoJsonPolygon box = new GeoJsonPolygon(ll, ul, ur, lr, ll);
        Criteria criteria = Criteria.where("location").within(box);
        if (filter.isInProgress()) {
            criteria = criteria.and("auctionStatus").is("진행");
        } else {
            criteria = criteria.and("auctionStatus").is("낙찰");
        }
        return criteria;
    }
}
