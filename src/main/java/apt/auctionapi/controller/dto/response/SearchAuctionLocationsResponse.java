package apt.auctionapi.controller.dto.response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import apt.auctionapi.entity.auction.AuctionLocation;

public record SearchAuctionLocationsResponse(
    Integer totalCount,
    List<InnerLocation> locations
) {

    private record InnerLocation(
        double longitude,
        double latitude
    ) {
        public static InnerLocation from(GeoJsonPoint geoJsonPoint) {
            return new InnerLocation(geoJsonPoint.getX(), geoJsonPoint.getY());
        }
    }

    public static SearchAuctionLocationsResponse from(List<AuctionLocation> locations) {
        List<InnerLocation> innerLocations = locations.stream()
            .map(loc -> InnerLocation.from(loc.getLocation()))
            .distinct()
            .toList();
        return new SearchAuctionLocationsResponse(innerLocations.size(), innerLocations);
    }
}
