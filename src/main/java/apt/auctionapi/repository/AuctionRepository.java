package apt.auctionapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import apt.auctionapi.entity.Auction;

@Repository
public interface AuctionRepository extends MongoRepository<Auction, String> {

    @Query("{ 'location.x': { $gte: ?0, $lte: ?1 }, 'location.y': { $gte: ?2, $lte: ?3 } }")
    List<Auction> findAuctionsWithinBounds(double minLat, double maxLat, double minLng, double maxLng);
}
