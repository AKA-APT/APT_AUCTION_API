package apt.auctionapi.entity.auction.sources;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;

/**
 * 사건 기본 정보 (Case Base Info)
 * <p>
 * 이 클래스는 MongoDB 컬렉션의 "csBaseInfo" 필드를 매핑하며,
 * 사건의 법원, 사건 번호, 사건 종류, 접수 날짜, 청구 금액 등의 정보를 포함합니다.
 */
@Getter
public class CaseBaseInfo {

    /**
     * 법원 코드 (예: "B000210")
     */
    @Field("cortOfcCd")
    private String courtCode;

    /**
     * 법원 이름 (예: "서울중앙지방법원")
     */
    @Field("cortOfcNm")
    private String courtName;

    /**
     * 지원 법원 이름 (예: "서울중앙지방법원")
     */
    @Field("cortSptNm")
    private String supportCourtName;

    /**
     * 사건 번호 (예: "20240130112651")
     */
    @Field("csNo")
    private String caseNumber;

    /**
     * 사건 종류 (예: "부동산강제경매")
     */
    @Field("csNm")
    private String caseType;

    /**
     * 사건 접수 날짜 (예: "2024-06-25")
     */
    @Field("csRcptYmd")
    private LocalDate caseReceivedDate;

    /**
     * 경매 개시 날짜 (예: "2024-06-26")
     */
    @Field("csCmdcYmd")
    private LocalDate caseDecisionDate;

    /**
     * 청구 금액 (예: 100901368 원)
     */
    @Field("clmAmt")
    private BigDecimal claimAmount;

    /**
     * 경매 중지 상태 코드 (예: "03" → 경매 진행 중)
     */
    @Field("auctnSuspStatCd")
    private String auctionSuspensionStatus;

    /**
     * 담당 부서 코드 (예: "1011")
     */
    @Field("jdbnCd")
    private String departmentCode;

    /**
     * 경매 진행 부서명 (예: "경매11계")
     */
    @Field("cortAuctnJdbnNm")
    private String departmentName;

    /**
     * 담당 부서 전화번호 (예: "02-530-1817")
     */
    @Field("jdbnTelno")
    private String departmentPhone;

    /**
     * 사용자 사건 번호 (예: "2024타경112651")
     */
    @Field("userCsNo")
    private String userCaseNumber;
}
