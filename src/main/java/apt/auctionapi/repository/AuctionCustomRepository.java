package apt.auctionapi.repository;

import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import apt.auctionapi.controller.dto.request.SearchAuctionLocationsRequest;
import apt.auctionapi.controller.dto.request.SearchAuctionRequest;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.AuctionLocation;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuctionCustomRepository {

    private final MongoTemplate mongoTemplate;

    public List<Auction> findByLocationRange(SearchAuctionRequest filter) {
        Criteria criteria = buildCircleCriteria(filter);
        Query query = new Query(criteria);
        query.fields()
            .include("gdsDspslDxdyLst")
            .include("id")
            .include("csBaseInfo")
            .include("location")
            .include("dspslGdsDxdyInfo")
            .include("gdsDspslObjctLst")
            .include("aeeWevlMnpntLst")
            .include("auctionStatus");
        return mongoTemplate.find(query, Auction.class);
    }

    public List<AuctionLocation> findLightweightByLocationRange(SearchAuctionLocationsRequest filter) {
        Criteria criteria = buildCircleCriteria(filter);
        Query query = new Query(criteria);
        query.fields()
            .include("location")
            .include("auctionStatus");
        return mongoTemplate.find(query, AuctionLocation.class);
    }

    private Criteria buildCircleCriteria(SearchAuctionRequest filter) {
        // Bounding box corners
        double minLng = filter.lbLng();
        double maxLng = filter.rtLng();
        double minLat = filter.lbLat();
        double maxLat = filter.rtLat();

        // Center point
        double centerLng = (minLng + maxLng) / 2;
        double centerLat = (minLat + maxLat) / 2;

        // Convert degrees to kilometers
        double kmPerLat = 111;
        double kmPerLng = 111 * Math.cos(Math.toRadians(centerLat));

        // Width and height in km
        double widthKm = (maxLng - minLng) * kmPerLng;
        double heightKm = (maxLat - minLat) * kmPerLat;

        // Radius = half of diagonal
        double radiusKm = Math.sqrt(widthKm * widthKm + heightKm * heightKm) / 2;

        // Create circle (in kilometers)
        Circle sphere = new Circle(new Point(centerLng, centerLat), new Distance(radiusKm, Metrics.KILOMETERS));

        // Build criteria: withinSphere uses 2dsphere index
        Criteria criteria = Criteria.where("location").withinSphere(sphere);
        if (filter.isInProgress()) {
            criteria = criteria.and("auctionStatus").is("진행");
        } else {
            criteria = criteria.and("auctionStatus").is("낙찰");
        }
        return criteria;
    }

    private Criteria buildCircleCriteria(SearchAuctionLocationsRequest filter) {
        // Same logic for lightweight location search
        double minLng = filter.lbLng();
        double maxLng = filter.rtLng();
        double minLat = filter.lbLat();
        double maxLat = filter.rtLat();

        double centerLng = (minLng + maxLng) / 2;
        double centerLat = (minLat + maxLat) / 2;
        double kmPerLat = 111;
        double kmPerLng = 111 * Math.cos(Math.toRadians(centerLat));
        double widthKm = (maxLng - minLng) * kmPerLng;
        double heightKm = (maxLat - minLat) * kmPerLat;
        double radiusKm = Math.sqrt(widthKm * widthKm + heightKm * heightKm) / 2;
        Circle sphere = new Circle(new Point(centerLng, centerLat), new Distance(radiusKm, Metrics.KILOMETERS));

        Criteria criteria = Criteria.where("location").withinSphere(sphere);
        if (filter.isInProgress()) {
            criteria = criteria.and("auctionStatus").is("진행");
        } else {
            criteria = criteria.and("auctionStatus").is("낙찰");
        }
        return criteria;
    }
}
