package apt.auctionapi.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import apt.auctionapi.entity.AuctionDocument;

public interface AuctionRepositoryV2 extends MongoRepository<AuctionDocument, String> {
    Optional<AuctionDocument> findByAuctionId(String auctionId);
}