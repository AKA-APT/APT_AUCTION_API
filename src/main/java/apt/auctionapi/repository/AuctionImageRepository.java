package apt.auctionapi.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import apt.auctionapi.entity.AuctionDocument;

public interface AuctionImageRepository extends MongoRepository<AuctionDocument, String> {
    Optional<AuctionDocument> findByAuctionId(ObjectId auctionId);
}