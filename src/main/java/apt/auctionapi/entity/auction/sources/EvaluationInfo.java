package apt.auctionapi.entity.auction.sources;

import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * 감정 평가 정보 (Evaluation Info)
 * <p>
 * 이 클래스는 특정 사건과 관련된 감정 평가 정보를 포함합니다.
 * 평가 항목, 평가 내용 등의 데이터를 포함합니다.
 */
@Getter
public class EvaluationInfo {

    /**
     * 법원 코드 (예: "B000210")
     */
    @Field("cortOfcCd")
    private String courtCode;

    /**
     * 사건 번호 (예: "20240130112651")
     */
    @Field("csNo")
    private String caseNumber;

    /**
     * 감정 평가 항목 일련번호 (예: 1)
     */
    @Field("aeeWevlMnpntDtlSeq")
    private Integer evaluationSequence;

    /**
     * 감정 평가 분류 코드 (예: "00082001")
     */
    @Field("aeeWevlMnpntTbltDvsCd")
    @JsonIgnore
    private String evaluationCategoryCode;

    @Setter
    private String evaluationCategory;

    /**
     * 감정 평가 항목 코드 (예: "00083001")
     */
    @Field("aeeWevlMnpntItmCd")
    @JsonIgnore
    private String evaluationItemCode;

    @Setter
    private String evaluationItem;

    /**
     * 감정 평가 내용 (예: "본건은 서울특별시 관악구 신림동 소재 '서울조원초등학교' 남서측 인근에 위치하고 주변으로 단독주택, 공동주택, 주상용건물, 각종 상업용건물 등이 소재하는 지역으로 제반 입지여건은 보통시됨.")
     */
    @Field("aeeWevlMnpntCtt")
    private String evaluationContent;
}
