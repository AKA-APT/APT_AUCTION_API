package apt.auctionapi.controller.dto.response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public record SearchAuctionLocationsResponse(
    Integer totalCount,
    List<InnerLocation> locations
) {

    private record InnerLocation(
        double x,
        double y
    ) {
        public static InnerLocation from(GeoJsonPoint geoJsonPoint) {
            return new InnerLocation(geoJsonPoint.getX(), geoJsonPoint.getY());
        }
    }

    public static SearchAuctionLocationsResponse from(List<GeoJsonPoint> locations) {
        List<InnerLocation> innerLocations = new ArrayList<>();
        for (GeoJsonPoint location : locations) {
            InnerLocation from = InnerLocation.from(location);
            innerLocations.add(from);
        }
        return new SearchAuctionLocationsResponse(innerLocations.size(), innerLocations);
    }
}
