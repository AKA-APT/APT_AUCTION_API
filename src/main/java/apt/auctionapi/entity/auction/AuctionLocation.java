package apt.auctionapi.entity.auction;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;

@Getter
@Document(collection = "auctions_distinct")
public class AuctionLocation {

    @Field("location")
    private GeoJsonPoint location;
}
