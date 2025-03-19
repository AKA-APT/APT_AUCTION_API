package apt.auctionapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.AuctionSummary;

/**
 * 경매 데이터 리포지토리 (Auction Repository)
 * <p>
 * 이 인터페이스는 MongoDB 컬렉션 "detail_auctions"에 대한 데이터 액세스를 제공합니다.
 * 위도(latitude)와 경도(longitude) 범위를 기준으로 데이터를 조회하는 기능을 포함합니다.
 */
@Repository
public interface AuctionRepository extends MongoRepository<Auction, String> {

    Optional<Auction> findById(String id);
}
