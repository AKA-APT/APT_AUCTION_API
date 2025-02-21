package apt.auctionapi.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import apt.auctionapi.entity.auction_entity.AuctionObject;
import apt.auctionapi.entity.auction_entity.CaseBaseInfo;
import lombok.Getter;

@Getter
@Document(collection = "detail_auctions")
public class AuctionSummary {

    /**
     * MongoDB 문서의 고유 ID
     */
    @Id
    private String id;

    /**
     * 사건 기본 정보 (법원, 사건 번호, 사건 종류 등)
     */
    @Field("csBaseInfo")
    private CaseBaseInfo caseBaseInfo;

    /**
     * 경매 대상 물건 정보 리스트
     */
    private AuctionObject auctionObject;
}
