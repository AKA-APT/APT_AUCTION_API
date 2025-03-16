package apt.auctionapi.controller.dto.request;

import apt.auctionapi.domain.InvestmentTag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record AuctionSearchRequest(
        @Schema(description = "좌측 하단 위도", example = "37.5709061")
        double lbLat,

        @Schema(description = "좌측 하단 경도", example = "126.6675837")
        double lbLng,

        @Schema(description = "우측 상단 위도", example = "37.601581")
        double rtLat,

        @Schema(description = "우측 상단 경도", example = "126.738909")
        double rtLng,

        @Schema(description = "최소낙찰가", example = "50000000")
        Long minBidPrice,

        @Schema(description = "유찰횟수 필터 (0: 전부, 1-5: 해당 횟수, 6: 5회 이상)", example = "2")
        Integer failedBidCount,

        @Schema(
                description = "투자 유형 태그 필터 (쉼표로 구분)",
                allowableValues = {"수익형", "장기투자", "연금형", "저위험", "고급주거", "자가우선", "임대사업",
                        "정부지원형", "고위험", "단기투자", "갭투자", "재개발", "재건축", "분양권",
                        "상업용부동산", "공유오피스", "생애최초", "친환경", "소형부동산", "호텔숙박업",
                        "공유주택", "테마형부동산", "이색부동산", "공장산업단지", "물류센터",
                        "상업지구개발", "특수상업부동산", "토지투자", "농지투자", "임야투자"},
                type = "array"
        )
        List<String> investmentTags
) {
    // 기본값을 위한 컴팩트 생성자
    public AuctionSearchRequest {
        if (failedBidCount == null) {
            failedBidCount = 0;
        }
        if (investmentTags == null) {
            investmentTags = Collections.emptyList();
        }
    }

    // 투자 유형 태그 이름 목록을 반환
    public List<String> getInvestmentTagNames() {
        return investmentTags;
    }

    // 투자 유형 태그 Enum 목록을 반환
    public List<InvestmentTag> getInvestmentTagList() {
        if (investmentTags == null || investmentTags.isEmpty()) {
            return Collections.emptyList();
        }

        return investmentTags.stream()
                .map(tagName -> {
                    try {
                        return InvestmentTag.fromName(tagName);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
