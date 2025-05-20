package apt.auctionapi.domain;

import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.sources.AuctionObject;
import apt.auctionapi.entity.auction.sources.CaseBaseInfo;
import apt.auctionapi.entity.auction.sources.DisposalGoodsExecutionInfo;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;

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
    HIGH_RISK(9, "변동성", "변동성이 높은 지역과 부동산에 투자"),
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
     * 태그별 판별 기준을 담는 TagRule 클래스
     */
    private static class TagRule {
        private final String property; // 설명용
        private final Predicate<Auction> condition;
        private final int weight;

        public TagRule(String property, Predicate<Auction> condition, int weight) {
            this.property = property;
            this.condition = condition;
            this.weight = weight;
        }

        public boolean test(Auction auction) {
            return condition.test(auction);
        }

        public int getWeight() {
            return weight;
        }
    }

    /**
     * 태그별 판별 기준(속성, 조건, 가중치) 리스트
     */
    private static final Map<InvestmentTag, List<TagRule>> tagRules = Map.ofEntries(
            Map.entry(INCOME_GENERATING, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("오피스텔", "상가", "오피스", "원룸", "다세대", "상가건물", "근린생활시설").contains(type))
                            .orElse(false), 3),
                    new TagRule("appraisedValue", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAppraisedValue)
                            .map(v -> v.compareTo(new BigDecimal("300000000")) >= 0 && v.compareTo(new BigDecimal("1000000000")) <= 0)
                            .orElse(false), 1),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("역세권") || addr.contains("상권"))
                            .orElse(false), 1),
                    new TagRule("buildingStructure", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getBuildingStructure)
                            .map(bs -> bs.contains("오피스") || bs.contains("상가") || bs.contains("오피스텔"))
                            .orElse(false), 1),
                    new TagRule("임대차계약존재여부", auction -> false, 2), // TODO: 임대차계약 정보 추가시 구현
                    new TagRule("월세/리스수익", auction -> false, 2) // TODO: 월세/리스 정보 추가시 구현
            )),
            Map.entry(LONG_TERM_INVESTMENT, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("아파트", "주택", "토지", "프리미엄 주거").contains(type))
                            .orElse(false), 2),
                    new TagRule("appraisedValue", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAppraisedValue)
                            .map(v -> v.compareTo(new BigDecimal("1000000000")) > 0)
                            .orElse(false), 2),
                    new TagRule("caseType", auction -> Optional.ofNullable(auction.getCaseBaseInfo())
                            .map(CaseBaseInfo::getCaseType)
                            .map(type -> !type.contains("강제경매"))
                            .orElse(false), 1),
                    new TagRule("유찰횟수", auction -> Optional.ofNullable(auction.getAuctionScheduleList())
                            .map(list -> list.stream().filter(s -> "002".equals(s.getAuctionResultCode())).count())
                            .map(cnt -> cnt <= 1)
                            .orElse(false), 1),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("신도시") || addr.contains("개발예정"))
                            .orElse(false), 1)
            )),
            Map.entry(HIGH_RISK, List.of(
                    new TagRule("유찰횟수", auction -> Optional.ofNullable(auction.getAuctionScheduleList())
                            .map(list -> list.stream().filter(s -> "002".equals(s.getAuctionResultCode())).count())
                            .map(cnt -> cnt >= 3)
                            .orElse(false), 2),
                    new TagRule("caseType", auction -> Optional.ofNullable(auction.getCaseBaseInfo())
                            .map(CaseBaseInfo::getCaseType)
                            .map(type -> type.contains("임의경매"))
                            .orElse(false), 2),
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("공장", "창고", "임야", "레저시설").contains(type))
                            .orElse(false), 1),
                    new TagRule("appraisedValue", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAppraisedValue)
                            .map(v -> v.compareTo(new BigDecimal("300000000")) < 0 || v.compareTo(new BigDecimal("30000000000")) > 0)
                            .orElse(false), 1)
            )),
            Map.entry(LOW_RISK, List.of(
                    new TagRule("유찰횟수", auction -> Optional.ofNullable(auction.getAuctionScheduleList())
                            .map(list -> list.stream().filter(s -> "002".equals(s.getAuctionResultCode())).count())
                            .map(cnt -> cnt == 0)
                            .orElse(false), 2),
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("아파트", "오피스텔", "프리미엄 주거").contains(type))
                            .orElse(false), 1)
            )),
            Map.entry(PREMIUM_RESIDENTIAL, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> type.contains("프리미엄") || type.contains("고급") || type.contains("아파트"))
                            .orElse(false), 2),
                    new TagRule("appraisedValue", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAppraisedValue)
                            .map(v -> v.compareTo(new BigDecimal("1000000000")) > 0)
                            .orElse(false), 2)
            )),
            Map.entry(SELF_OCCUPANCY, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("아파트", "주택", "오피스텔").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("학군") || addr.contains("교통"))
                            .orElse(false), 2),
                    new TagRule("근저당권존재여부", auction -> false, 1) // TODO: 권리 분석 정보 추가 시 구현
            )),
            Map.entry(RENTAL_BUSINESS, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("다세대", "원룸", "오피스텔", "상가건물").contains(type))
                            .orElse(false), 3),
                    // 아래의 totalArea 속성은 AuctionObject에 없는 속성이므로 제거하거나 대체합니다
                    // appraisedValue 필드로 대체하여 일정 규모 이상의 건물 판단
                    new TagRule("건물규모", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAppraisedValue)
                            .map(value -> value.compareTo(new BigDecimal("300000000")) >= 0)
                            .orElse(false), 2),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("대학가") || addr.contains("산업단지") || addr.contains("역세권"))
                            .orElse(false), 2)
            )),
            Map.entry(GOVERNMENT_SUPPORTED, List.of(
                    new TagRule("caseType", auction -> Optional.ofNullable(auction.getCaseBaseInfo())
                            .map(CaseBaseInfo::getCaseType)
                            .map(type -> type.contains("공공임대") || type.contains("장기전세"))
                            .orElse(false), 3)
            )),
            Map.entry(SHORT_TERM_INVESTMENT, List.of(
                    new TagRule("유찰횟수", auction -> Optional.ofNullable(auction.getAuctionScheduleList())
                            .map(list -> list.stream().filter(s -> "002".equals(s.getAuctionResultCode())).count())
                            .map(cnt -> cnt == 1)
                            .orElse(false), 2),
                    new TagRule("appraisedValue", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAppraisedValue)
                            .map(v -> v.compareTo(new BigDecimal("100000000")) >= 0 && v.compareTo(new BigDecimal("500000000")) <= 0)
                            .orElse(false), 2),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("급매") || addr.contains("시세차익"))
                            .orElse(false), 1)
            )),
            Map.entry(GAP_INVESTMENT, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("아파트", "오피스텔").contains(type))
                            .orElse(false), 3),
                    new TagRule("전세가율", auction -> false, 3), // TODO: 전세가 정보 추가 시 구현
                    new TagRule("appraisedValue", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAppraisedValue)
                            .map(v -> v.compareTo(new BigDecimal("300000000")) >= 0 && v.compareTo(new BigDecimal("800000000")) <= 0)
                            .orElse(false), 2)
            )),
            Map.entry(REDEVELOPMENT, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("주택", "빌라", "토지").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("재개발") || addr.contains("구역"))
                            .orElse(false), 3),
                    // buildingRegisterInfo 및 buildYear는 AuctionObject에 없는 필드이므로 수정
                    // buildingStructure 필드로 대체하여 오래된 건물 여부 판단
                    new TagRule("건물상태", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getBuildingStructure)
                            .map(structure -> structure.contains("노후") || structure.contains("오래된"))
                            .orElse(false), 2)
            )),
            Map.entry(RECONSTRUCTION, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("아파트").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("재건축") || addr.contains("단지"))
                            .orElse(false), 3),
                    // buildingRegisterInfo 및 buildYear는 AuctionObject에 없는 필드이므로 수정
                    // buildingStructure 필드로 대체하여 오래된 건물 여부 판단
                    new TagRule("건물상태", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getBuildingStructure)
                            .map(structure -> structure.contains("노후") || structure.contains("오래된"))
                            .orElse(false), 2)
            )),
            Map.entry(SUBSCRIPTION_RIGHTS, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("분양권").contains(type))
                            .orElse(false), 3)
                    // TODO: 분양권 관련 상세 정보 추가 시 규칙 보완
            )),
            Map.entry(COMMERCIAL_REAL_ESTATE, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("상가", "오피스", "공장", "창고", "상가건물", "근린생활시설").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("상권") || addr.contains("오피스지구"))
                            .orElse(false), 2)
            )),
            Map.entry(SHARED_OFFICE, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("오피스", "상가", "근린생활시설").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("역세권") || addr.contains("IT벨리"))
                            .orElse(false), 2),
                    // Floor 필드가 없으므로 buildingStructure로 대체
                    new TagRule("건물구조", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getBuildingStructure)
                            .map(structure -> structure.contains("지상") || structure.contains("전체"))
                            .orElse(false), 1)
            )),
            Map.entry(FIRST_TIME_BUYER, List.of(
                    new TagRule("appraisedValue", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAppraisedValue)
                            .map(v -> v.compareTo(new BigDecimal("100000000")) >= 0 && v.compareTo(new BigDecimal("500000000")) <= 0)
                            .orElse(false), 3),
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("아파트", "오피스텔", "빌라").contains(type))
                            .orElse(false), 2)
            )),
            Map.entry(ECO_FRIENDLY, List.of(
                    new TagRule("설명", auction -> Optional.ofNullable(auction.getDisposalGoodsExecutionInfo())
                            .map(DisposalGoodsExecutionInfo::getRemarks)
                            .map(info -> info != null && (info.contains("태양광") || info.contains("에너지") || info.contains("친환경")))
                            .orElse(false), 3)
                    // TODO: 에너지 효율 등급 정보 추가 시 규칙 보완
            )),
            Map.entry(SMALL_REAL_ESTATE, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("오피스텔", "도시형 생활주택", "원룸").contains(type))
                            .orElse(false), 3),
                    // totalArea 필드가 없으므로 appraisedValue로 대체하여 소규모 부동산 판단
                    new TagRule("감정가치", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAppraisedValue)
                            .map(value -> value.compareTo(new BigDecimal("200000000")) <= 0)
                            .orElse(false), 2)
            )),
            Map.entry(HOTEL_ACCOMMODATION, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("숙박시설", "모텔", "호텔", "리조트").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("관광지") || addr.contains("해변"))
                            .orElse(false), 2)
            )),
            Map.entry(SHARED_HOUSING, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("주택", "다세대", "원룸").contains(type))
                            .orElse(false), 3),
                    // numberOfRooms 필드가 없으므로 buildingStructure로 대체
                    new TagRule("건물구조", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getBuildingStructure)
                            .map(structure -> structure.contains("다실") || structure.contains("복층"))
                            .orElse(false), 2)
            )),
            Map.entry(THEMED_REAL_ESTATE, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("단독주택", "토지", "숙박시설").contains(type))
                            .orElse(false), 2),
                    new TagRule("설명", auction -> Optional.ofNullable(auction.getDisposalGoodsExecutionInfo())
                            .map(DisposalGoodsExecutionInfo::getRemarks)
                            .map(info -> info != null && (info.contains("실버타운") || info.contains("한옥") || info.contains("펜션") || info.contains("캠핑장")))
                            .orElse(false), 3)
            )),
            Map.entry(UNIQUE_REAL_ESTATE, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("토지", "임야", "레저시설").contains(type))
                            .orElse(false), 2),
                    new TagRule("설명", auction -> Optional.ofNullable(auction.getDisposalGoodsExecutionInfo())
                            .map(DisposalGoodsExecutionInfo::getRemarks)
                            .map(info -> info != null && (info.contains("와이너리") || info.contains("목장") || info.contains("골프장")))
                            .orElse(false), 3)
            )),
            Map.entry(FACTORY_INDUSTRIAL_COMPLEX, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("공장", "창고", "산업용지").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("산업단지") || addr.contains("공업지역"))
                            .orElse(false), 3)
            )),
            Map.entry(LOGISTICS_CENTER, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("창고", "물류센터").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("고속도로") || addr.contains("IC"))
                            .orElse(false), 3)
            )),
            Map.entry(COMMERCIAL_DISTRICT_DEVELOPMENT, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("상가", "토지", "상가건물").contains(type))
                            .orElse(false), 2),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("상권 활성화") || addr.contains("신규 상업지구"))
                            .orElse(false), 3)
            )),
            Map.entry(SPECIAL_COMMERCIAL_REAL_ESTATE, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("상가", "근린생활시설").contains(type))
                            .orElse(false), 2),
                    new TagRule("설명", auction -> Optional.ofNullable(auction.getDisposalGoodsExecutionInfo())
                            .map(DisposalGoodsExecutionInfo::getRemarks)
                            .map(info -> info != null && (info.contains("병원") || info.contains("학원") || info.contains("프랜차이즈")))
                            .orElse(false), 3)
            )),
            Map.entry(LAND_INVESTMENT, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("토지", "임야", "농지").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("개발예정") || addr.contains("토지거래허가구역"))
                            .orElse(false), 3)
            )),
            Map.entry(FARMLAND_INVESTMENT, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("농지", "전", "답", "과수원").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("귀농") || addr.contains("스마트팜") || addr.contains("태양광"))
                            .orElse(false), 2)
            )),
            Map.entry(FOREST_INVESTMENT, List.of(
                    new TagRule("propertyType", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getPropertyType)
                            .map(type -> List.of("임야", "산", "숲").contains(type))
                            .orElse(false), 3),
                    new TagRule("address", auction -> Optional.ofNullable(auction.getAuctionObjectList())
                            .filter(list -> !list.isEmpty())
                            .map(List::getFirst)
                            .map(AuctionObject::getAddress)
                            .map(addr -> addr.contains("친환경") || addr.contains("개발") || addr.contains("산림"))
                            .orElse(false), 2)
            ))
    );

    // tagRules를 활용한 예시 메서드 (실제 사용처에 맞게 구현 필요)
    public static List<InvestmentTag> getTagsForAuction(Auction auction) {
        // 각 태그별로 가중치 합산 결과를 저장
        List<Map.Entry<InvestmentTag, Integer>> scoredTags = new ArrayList<>();
        for (Map.Entry<InvestmentTag, List<TagRule>> entry : tagRules.entrySet()) {
            int score = entry.getValue().stream().mapToInt(rule -> rule.test(auction) ? rule.getWeight() : 0).sum();
            if (score > 0) {
                scoredTags.add(Map.entry(entry.getKey(), score));
            }
        }
        // 가중치 내림차순 정렬 후 상위 5개만 추출
        return scoredTags.stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static InvestmentTag fromName(String name) {
        for (InvestmentTag tag : InvestmentTag.values()) {
            if (tag.getName().equals(name)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Unknown investment tag: " + name);
    }

    // Auction 객체로부터 적합한 InvestmentTag 리스트를 반환하는 메서드
    public static List<InvestmentTag> from(Auction auction) {
        return getTagsForAuction(auction);
    }
}
