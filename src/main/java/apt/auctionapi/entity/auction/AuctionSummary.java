package apt.auctionapi.entity.auction;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import apt.auctionapi.entity.auction.sources.AuctionObject;
import apt.auctionapi.entity.auction.sources.CaseBaseInfo;
import lombok.Getter;

/**
 * 경매 데이터 엔티티 요약 엔티티 (AuctionSummary Entity)
 * <p>
 * 이 클래스는 MongoDB 컬렉션 "auctions"에 저장된 경매 데이터를 매핑하는 엔티티입니다.
 * 사건 기본 정보, 경매 일정, 감정 평가, 주변 통계 등 경매와 관련된 모든 데이터를 포함합니다.
 */
@Getter
@Document(collection = "auctions")
public class AuctionSummary {

    /**
     * MongoDB 문서의 고유 ID
     */
    @Id
    private String id;

    /**
     * 사건 기본 정보 (법원, 사건 번호, 사건 종류 등)
     */
    private CaseBaseInfo caseBaseInfo;

    /**
     * 경매 대상 물건 정보 리스트
     */
    private AuctionObject auctionObject;

    /**
     * 경매 대상 물건 위도/경도 좌표
     */
    private GeoJsonPoint location;
}
