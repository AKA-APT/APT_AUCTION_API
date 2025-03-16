package apt.auctionapi.domain;

import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.sources.AuctionObject;
import apt.auctionapi.entity.auction.sources.DisposalGoodsExecutionInfo;

import java.math.BigDecimal;
import java.util.*;

/**
 * 부동산 투자 유형 (Investment Tag)
 */
public enum InvestmentTag {
    INCOME_GENERATING(1, "수익형", "월세, 리스 수익을 기반으로 안정적인 현금 흐름 확보"),
    LONG_TERM_INVESTMENT(2, "장기투자", "가치 상승을 기다리며 장기간 보유"),
    PENSION_TYPE(3, "연금형", "노후 대비를 위한 부동산 연금 설계"),
    LOW_RISK(4, "저위험", "공실률이 낮고 입지가 탄탄한 부동산 선호"),
    PREMIUM_RESIDENTIAL(5, "고급주거", "프리미엄 아파트, 주택을 장기 보유"),
    SELF_OCCUPANCY(6, "자가우선", "실거주 목적이 우선, 이후 가치 상승 고려"),
    RENTAL_BUSINESS(7, "임대사업", "다세대, 원룸, 오피스텔을 다수 보유하여 안정적 수익 창출"),
    GOVERNMENT_SUPPORTED(8, "정부지원형", "공공 임대주택, 장기전세주택 등 정책적 혜택을 활용"),
    HIGH_RISK(9, "고위험", "변동성이 높은 지역과 부동산에 투자"),
    SHORT_TERM_INVESTMENT(10, "단기투자", "단기간 내 차익 실현을 목표로 하는 투자"),
    GAP_INVESTMENT(11, "갭투자", "적은 자본으로 레버리지를 활용해 매수"),
    REDEVELOPMENT(12, "재개발", "신축 예정 지역을 미리 선점하여 투자"),
    RECONSTRUCTION(13, "재건축", "오래된 아파트를 새롭게 개발하는 프로젝트 투자"),
    SUBSCRIPTION_RIGHTS(14, "분양권", "청약 당첨 후 프리미엄을 노린 거래"),
    COMMERCIAL_REAL_ESTATE(15, "상업용부동산", "오피스, 상가, 공장, 창고 등에 투자"),
    SHARED_OFFICE(16, "공유오피스", "스타트업·프리랜서를 위한 공간 임대"),
    FIRST_TIME_BUYER(17, "생애최초", "첫 부동산 투자로 실거주+자산 형성 목표"),
    ECO_FRIENDLY(18, "친환경", "ESG, 태양광 주택, 에너지 절감형 부동산 선호"),
    SMALL_REAL_ESTATE(19, "소형부동산", "오피스텔, 도시형 생활주택 등 소규모 부동산 선호"),
    HOTEL_ACCOMMODATION(20, "호텔숙박업", "에어비앤비, 모텔, 리조트 등 운영"),
    SHARED_HOUSING(21, "공유주택", "쉐어하우스, 코리빙(Co-living) 공간 투자"),
    THEMED_REAL_ESTATE(22, "테마형부동산", "실버타운, 한옥, 펜션, 캠핑장 등 특화된 부동산 투자"),
    UNIQUE_REAL_ESTATE(23, "이색부동산", "와이너리, 목장, 골프장, 레저시설 등 특수 부동산"),
    FACTORY_INDUSTRIAL_COMPLEX(24, "공장산업단지", "물류창고, 공장, 제조업 부동산 선호"),
    LOGISTICS_CENTER(25, "물류센터", "전자상거래 증가로 물류 창고 및 유통센터 확보"),
    COMMERCIAL_DISTRICT_DEVELOPMENT(26, "상업지구개발", "도심 내 상권 활성화 지역 투자"),
    SPECIAL_COMMERCIAL_REAL_ESTATE(27, "특수상업부동산", "병원, 학원, 카페, 프랜차이즈 매장 등"),
    LAND_INVESTMENT(28, "토지투자", "개발 가능성이 높은 땅에 투자"),
    FARMLAND_INVESTMENT(29, "농지투자", "귀농 목적, 태양광 발전, 스마트팜 운영 목적"),
    FOREST_INVESTMENT(30, "임야투자", "숲, 산을 활용한 친환경 개발"),
    ;

    private final int id;
    private final String name;
    private final String description;

    InvestmentTag(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Auction 객체로부터 적합한 투자 유형 태그 목록을 반환합니다.
     *
     * @param auction 분석할 경매 객체
     * @return 추천 투자 유형 태그 목록 (최대 5개)
     */
    public static List<InvestmentTag> from(Auction auction) {
        if (auction == null || auction.getAuctionObjectList() == null || auction.getAuctionObjectList().isEmpty()) {
            return Collections.emptyList();
        }

        // 첫 번째 경매 대상 물건 정보 가져오기
        AuctionObject auctionObject = auction.getAuctionObjectList().getFirst();

        // 추천 태그를 저장할 Set (중복 방지)

        // 1. 물건 종류 기반 태그 추가
        String propertyType = auctionObject.getPropertyType(); // rletDvsDts 필드
        Set<InvestmentTag> recommendedTags = new HashSet<>(getRecommendedTagsByPropertyType(propertyType));

        // 2. 가격 기반 태그 추가
        BigDecimal appraisedValue = auctionObject.getAppraisedValue(); // aeeEvlAmt 필드
        if (appraisedValue != null) {
            if (appraisedValue.compareTo(new BigDecimal("1000000000")) > 0) { // 10억 이상
                recommendedTags.add(PREMIUM_RESIDENTIAL);
                recommendedTags.add(LONG_TERM_INVESTMENT);
            } else if (appraisedValue.compareTo(new BigDecimal("300000000")) < 0) { // 3억 미만
                recommendedTags.add(GAP_INVESTMENT);
                recommendedTags.add(SMALL_REAL_ESTATE);
            }
        }

        // 3. 주소 기반 태그 추가
        String address = auctionObject.getAddress(); // userPrintSt 필드
        if (address != null) {
            // 재개발/재건축 지역 확인
            if (address.contains("재개발") || address.contains("정비구역")) {
                recommendedTags.add(REDEVELOPMENT);
            }

            // 상업 지역 확인
            if (address.contains("상업지구") || address.contains("상업지역")) {
                recommendedTags.add(COMMERCIAL_DISTRICT_DEVELOPMENT);
            }

            // 산업 단지 확인
            if (address.contains("산업단지") || address.contains("공단")) {
                recommendedTags.add(FACTORY_INDUSTRIAL_COMPLEX);
            }
        }

        // 4. 건물 구조 기반 태그 추가
        String buildingStructure = auctionObject.getBuildingStructure(); // pjbBuldList 필드
        if (buildingStructure != null && (buildingStructure.contains("한옥") || buildingStructure.contains("목조"))) {
            recommendedTags.add(UNIQUE_REAL_ESTATE);
            recommendedTags.add(ECO_FRIENDLY);
        }


        // 5. 유찰 횟수 기반 태그 추가
        int ruptureCount = getRuptureCount(auction);
        if (ruptureCount >= 3) {
            recommendedTags.add(HIGH_RISK);
            recommendedTags.add(SHORT_TERM_INVESTMENT);
        } else if (ruptureCount == 0) {
            recommendedTags.add(LOW_RISK);
        }

        // 6. 토지 이용 코드 기반 태그 추가
        String landUseCode = auctionObject.getLandUseCode(); // lclDspslGdsLstUsgCd 필드
        if (landUseCode != null) {
            // 농지 코드 확인 (예시 코드, 실제 코드는 시스템에 맞게 수정 필요)
            if (landUseCode.startsWith("2")) {
                recommendedTags.add(FARMLAND_INVESTMENT);
            }
            // 임야 코드 확인
            else if (landUseCode.startsWith("3")) {
                recommendedTags.add(FOREST_INVESTMENT);
            }
        }

        // 7. 사건 종류 기반 태그 추가
        if (auction.getCaseBaseInfo() != null) {
            String caseType = auction.getCaseBaseInfo().getCaseType(); // csNm 필드
            if (caseType != null && caseType.contains("임의경매")) {
                recommendedTags.add(HIGH_RISK);
            }

        }

        // 8. 경매 진행 정보 기반 태그 추가
        if (auction.getDisposalGoodsExecutionInfo() != null) {
            DisposalGoodsExecutionInfo executionInfo = auction.getDisposalGoodsExecutionInfo();

            // 낙찰가와 감정가 비교로 갭 투자 가능성 확인
            if (executionInfo.getFirstAuctionPrice() != null &&
                    executionInfo.getAppraisedValue() != null &&
                    executionInfo.getFirstAuctionPrice().compareTo(
                            executionInfo.getAppraisedValue().multiply(new BigDecimal("0.7"))) < 0) {
                recommendedTags.add(GAP_INVESTMENT);
            }
        }

        // 최대 5개까지만 반환 (우선순위에 따라 정렬 가능)
        return recommendedTags.stream()
                .limit(5)
                .toList();
    }

    // 기존 유찰 횟수 계산 메서드
    private static Integer getRuptureCount(Auction auction) {
        if (auction == null || auction.getAuctionScheduleList() == null) {
            return 0; // auction 또는 일정 리스트가 없으면 0 반환
        }

        return (int) auction.getAuctionScheduleList().stream()
                .filter(schedule -> "002".equals(schedule.getAuctionResultCode())) // 유찰 코드 필터링
                .count();
    }

    // 물건 종류에 따른 추천 태그 반환
    public static List<InvestmentTag> getRecommendedTagsByPropertyType(String propertyType) {
        if (propertyType == null) {
            return Collections.emptyList();
        }

        return switch (propertyType) {
            case "아파트" ->
                    Arrays.asList(SELF_OCCUPANCY, LONG_TERM_INVESTMENT, GAP_INVESTMENT, PREMIUM_RESIDENTIAL, RECONSTRUCTION);
            case "오피스텔" ->
                    Arrays.asList(SMALL_REAL_ESTATE, RENTAL_BUSINESS, SHORT_TERM_INVESTMENT, INCOME_GENERATING, GAP_INVESTMENT);
            case "상가", "상가건물" ->
                    Arrays.asList(INCOME_GENERATING, COMMERCIAL_REAL_ESTATE, SPECIAL_COMMERCIAL_REAL_ESTATE, COMMERCIAL_DISTRICT_DEVELOPMENT);
            case "토지", "대지" ->
                    Arrays.asList(LAND_INVESTMENT, REDEVELOPMENT, ECO_FRIENDLY, COMMERCIAL_DISTRICT_DEVELOPMENT);
            case "단독주택", "주택" ->
                    Arrays.asList(SELF_OCCUPANCY, REDEVELOPMENT, RECONSTRUCTION, PREMIUM_RESIDENTIAL, ECO_FRIENDLY);
            case "다가구주택", "다세대주택" -> Arrays.asList(RENTAL_BUSINESS, INCOME_GENERATING, GAP_INVESTMENT, SHARED_HOUSING);
            case "빌라" -> Arrays.asList(SELF_OCCUPANCY, RENTAL_BUSINESS, GAP_INVESTMENT, SMALL_REAL_ESTATE);
            case "연립주택" -> Arrays.asList(SELF_OCCUPANCY, RENTAL_BUSINESS, RECONSTRUCTION, SMALL_REAL_ESTATE);
            case "상가주택" -> Arrays.asList(INCOME_GENERATING, SELF_OCCUPANCY, SPECIAL_COMMERCIAL_REAL_ESTATE);
            case "근린생활시설" -> Arrays.asList(COMMERCIAL_REAL_ESTATE, INCOME_GENERATING, SPECIAL_COMMERCIAL_REAL_ESTATE);
            case "사무실", "오피스" -> Arrays.asList(COMMERCIAL_REAL_ESTATE, SHARED_OFFICE, INCOME_GENERATING);
            case "공장", "공장용지" -> Arrays.asList(FACTORY_INDUSTRIAL_COMPLEX, INCOME_GENERATING, HIGH_RISK);
            case "창고", "물류창고" -> Arrays.asList(LOGISTICS_CENTER, INCOME_GENERATING, COMMERCIAL_REAL_ESTATE);
            case "농지", "농지용지", "전", "답" -> Arrays.asList(FARMLAND_INVESTMENT, ECO_FRIENDLY, LAND_INVESTMENT);
            case "임야", "산" -> Arrays.asList(FOREST_INVESTMENT, ECO_FRIENDLY, UNIQUE_REAL_ESTATE);
            case "펜션", "리조트" ->
                    Arrays.asList(THEMED_REAL_ESTATE, HOTEL_ACCOMMODATION, INCOME_GENERATING, UNIQUE_REAL_ESTATE);
            case "숙박시설", "모텔", "호텔" -> Arrays.asList(HOTEL_ACCOMMODATION, INCOME_GENERATING, COMMERCIAL_REAL_ESTATE);
            case "실버타운", "요양시설" -> Arrays.asList(THEMED_REAL_ESTATE, INCOME_GENERATING, PENSION_TYPE);
            case "한옥" -> Arrays.asList(UNIQUE_REAL_ESTATE, THEMED_REAL_ESTATE, PREMIUM_RESIDENTIAL, ECO_FRIENDLY);
            case "캠핑장" -> Arrays.asList(UNIQUE_REAL_ESTATE, THEMED_REAL_ESTATE, INCOME_GENERATING);
            case "골프장", "레저시설" -> Arrays.asList(UNIQUE_REAL_ESTATE, HIGH_RISK, INCOME_GENERATING);
            case "병원", "의료시설" ->
                    Arrays.asList(SPECIAL_COMMERCIAL_REAL_ESTATE, INCOME_GENERATING, COMMERCIAL_REAL_ESTATE);
            case "학원", "교육시설" -> Arrays.asList(SPECIAL_COMMERCIAL_REAL_ESTATE, INCOME_GENERATING);
            case "카페", "음식점" -> Arrays.asList(SPECIAL_COMMERCIAL_REAL_ESTATE, INCOME_GENERATING, SMALL_REAL_ESTATE);
            case "프랜차이즈" -> Arrays.asList(SPECIAL_COMMERCIAL_REAL_ESTATE, INCOME_GENERATING, COMMERCIAL_REAL_ESTATE);
            case "도시형생활주택" -> Arrays.asList(SMALL_REAL_ESTATE, RENTAL_BUSINESS, GAP_INVESTMENT, SHARED_HOUSING);
            case "공공임대주택", "장기전세주택" -> Arrays.asList(GOVERNMENT_SUPPORTED, LOW_RISK, SELF_OCCUPANCY);
            case "분양권" -> Arrays.asList(SUBSCRIPTION_RIGHTS, SHORT_TERM_INVESTMENT, FIRST_TIME_BUYER);
            case "재개발", "재개발지역" -> Arrays.asList(REDEVELOPMENT, HIGH_RISK, SHORT_TERM_INVESTMENT);
            case "재건축", "재건축지역" -> Arrays.asList(RECONSTRUCTION, HIGH_RISK, SHORT_TERM_INVESTMENT);
            case "태양광발전소", "태양광부지" -> Arrays.asList(ECO_FRIENDLY, INCOME_GENERATING, FARMLAND_INVESTMENT);
            case "상업지구", "상업지역" ->
                    Arrays.asList(COMMERCIAL_DISTRICT_DEVELOPMENT, COMMERCIAL_REAL_ESTATE, INCOME_GENERATING);
            case "주상복합" -> Arrays.asList(SELF_OCCUPANCY, COMMERCIAL_REAL_ESTATE, PREMIUM_RESIDENTIAL);
            case "지식산업센터", "아파트형공장" ->
                    Arrays.asList(FACTORY_INDUSTRIAL_COMPLEX, COMMERCIAL_REAL_ESTATE, INCOME_GENERATING);
            default ->
                // 기본 추천 태그 (모든 부동산 유형에 적용 가능한 일반적인 태그)
                    Arrays.asList(INCOME_GENERATING, LONG_TERM_INVESTMENT, SHORT_TERM_INVESTMENT, LOW_RISK, HIGH_RISK);
        };
    }

    public static InvestmentTag fromName(String name) {
        for (InvestmentTag tag : InvestmentTag.values()) {
            if (tag.getName().equals(name)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Unknown investment tag: " + name);
    }
}
