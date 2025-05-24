package apt.auctionapi.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import apt.auctionapi.entity.AuctionDetail;

public interface AuctionDetailRepository extends MongoRepository<AuctionDetail, String> {
    Optional<AuctionDetail> findByAuctionId(ObjectId auctionId);
}
