package apt.auctionapi.entity;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.Getter;

@Document(collection = "auctions_richgo")
@Getter
public class AuctionDocument {

    @Id
    private String id;

    @Field("auction_id")
    private ObjectId auctionId;

    private String caseSite;

    private String caseName;

    private String itemNumber;

    private int photoCount;
}
