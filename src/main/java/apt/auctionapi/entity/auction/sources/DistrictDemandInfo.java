package apt.auctionapi.entity.auction.sources;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

/**
 * 집행문 발부 정보 (District Demand Info)
 * <p>
 * 이 클래스는 경매 사건의 집행문 발부 정보를 나타내며,
 * 사건과 관련된 집행문 코드 및 최종 발행 날짜를 포함합니다.
 */
@Getter
public class DistrictDemandInfo {

    /**
     * 집행문 구분 코드 (예: "021")
     */
    @Field("orddcsDvsCd")
    private String demandCode;

    /**
     * 최종 집행문 발행 날짜 (예: "2024-09-12")
     */
    @Field("dstrtDemnLstprdYmd")
    private LocalDate lastIssuedDate;
}
