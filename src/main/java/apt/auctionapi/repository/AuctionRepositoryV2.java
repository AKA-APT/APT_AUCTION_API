package apt.auctionapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import apt.auctionapi.entity.AuctionEntity;
import apt.auctionapi.entity.AuctionSummary;

/**
 * 경매 데이터 리포지토리 (Auction Repository)
 * <p>
 * 이 인터페이스는 MongoDB 컬렉션 "detail_auctions"에 대한 데이터 액세스를 제공합니다.
 * 위도(latitude)와 경도(longitude) 범위를 기준으로 데이터를 조회하는 기능을 포함합니다.
 */
@Repository
public interface AuctionRepositoryV2 extends MongoRepository<AuctionEntity, String> {

    @Aggregation(pipeline = {
        "{ $unwind: '$gdsDspslObjctLst' }",  // 배열을 개별 문서로 변환
        """
            { $match: { \
            'gdsDspslObjctLst.stYcrd': { $gte: ?0, $lte: ?2 }, \
            'gdsDspslObjctLst.stXcrd': { $gte: ?1, $lte: ?3 } \
            } }""",
        """
            { $project: { \
            'id': 1, \
            'caseBaseInfo': 1, \
            'auctionObject': '$gdsDspslObjctLst' \
            } }"""
    })
    List<AuctionSummary> findByLocationRange(double lbLat, double lbLng, double rtLat, double rtLng);
}
