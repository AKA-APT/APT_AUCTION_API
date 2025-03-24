package apt.auctionapi.entity.auction;

import java.util.HashMap;
import java.util.Map;

/**
 * 경매 코드 매핑 유틸리티
 * <p>
 * 이 클래스는 경매 일정과 관련된 다양한 코드값의 매핑을 제공합니다.
 * 경매 종류, 결과, 상태 등의 코드에 대한 의미를 조회할 수 있습니다.
 */

public class AuctionCodeMapper {

    private static final Map<String, String> AUCTION_KIND_MAP;
    private static final Map<String, String> AUCTION_RESULT_MAP;
    private static final Map<String, String> EVALUATION_ITEM_MAP;
    private static final Map<String, String> EVALUATION_TABLE_TYPE_MAP;
    private static final Map<String, String> AUCTION_GOODS_USAGE_MAP;

    static {
        AUCTION_KIND_MAP = Map.of("01", "매각기일", "02", "매각결정기일", "03", "대금지급기한", "04", "대금지급및 배당기일", "05", "배당기일", "06",
            "일부배당", "07", "일부배당 및 상계", "08", "심문기일", "09", "추가배당기일", "11", "개찰기일");

        AUCTION_RESULT_MAP = Map.ofEntries(Map.entry("000", "매각준비"), Map.entry("001", "매각"), Map.entry("002", "유찰"),
            Map.entry("003", "최고가매각허가결정"), Map.entry("004", "차순위매각허가결정"), Map.entry("005", "최고가매각불허가결정"),
            Map.entry("006", "차순위매각불허가결정"), Map.entry("007", "기한변경"), Map.entry("008", "추후지정"), Map.entry("009", "납부"),
            Map.entry("010", "미납"), Map.entry("011", "기한후납부"), Map.entry("012", "상계허가"), Map.entry("013", "진행"),
            Map.entry("014", "변경"), Map.entry("015", "배당종결"), Map.entry("016", "배당불가"), Map.entry("017", "최고가매각허가취소결정"),
            Map.entry("018", "차순위매각허가취소결정"));

        // 감정평가 요항 코드 매핑
        EVALUATION_ITEM_MAP = new HashMap<>();
        EVALUATION_ITEM_MAP.put("00083001", "위치 및 주위환경");
        EVALUATION_ITEM_MAP.put("00083002", "위치 및 부근의 상황");
        EVALUATION_ITEM_MAP.put("00083003", "교통상황");
        EVALUATION_ITEM_MAP.put("00083004", "인접 도로상태");
        EVALUATION_ITEM_MAP.put("00083005", "인접 도로상태등");
        EVALUATION_ITEM_MAP.put("00083006", "이용상태");
        EVALUATION_ITEM_MAP.put("00083007", "이용상태 및 장래성");
        EVALUATION_ITEM_MAP.put("00083008", "형태 및 이용상태");
        EVALUATION_ITEM_MAP.put("00083009", "토지의 형상 및 이용상태");
        EVALUATION_ITEM_MAP.put("00083010", "토지의 상황");
        EVALUATION_ITEM_MAP.put("00083011", "토지이용계획 및 제한상태");
        EVALUATION_ITEM_MAP.put("00083012", "도시계획 및 기타공법상의 제한사항");
        EVALUATION_ITEM_MAP.put("00083013", "제시목록 외의 물건");
        EVALUATION_ITEM_MAP.put("00083014", "공부와의 차이");
        EVALUATION_ITEM_MAP.put("00083015", "건물의 구조");
        EVALUATION_ITEM_MAP.put("00083016", "건물의 구조 및 현상");
        EVALUATION_ITEM_MAP.put("00083017", "설비내역");
        EVALUATION_ITEM_MAP.put("00083018", "부합물 및 종물");
        EVALUATION_ITEM_MAP.put("00083019", "기계/기구의 현상");
        EVALUATION_ITEM_MAP.put("00083020", "공작물의 현상");
        EVALUATION_ITEM_MAP.put("00083021", "년식 및 주행거리");
        EVALUATION_ITEM_MAP.put("00083022", "색상");
        EVALUATION_ITEM_MAP.put("00083023", "관리상태");
        EVALUATION_ITEM_MAP.put("00083024", "사용연료");
        EVALUATION_ITEM_MAP.put("00083025", "유효검사기간");
        EVALUATION_ITEM_MAP.put("00083026", "기타참고사항(임대관례 및 기타)");
        EVALUATION_ITEM_MAP.put("00083027", "기타참고사항");
        EVALUATION_ITEM_MAP.put("00083028", "기타(옵션등)");
        EVALUATION_ITEM_MAP.put("00083029", "입지조건");
        EVALUATION_ITEM_MAP.put("00083030", "임지사항");
        EVALUATION_ITEM_MAP.put("00083031", "임목상황");
        EVALUATION_ITEM_MAP.put("00083032", "사업체의 개요");
        EVALUATION_ITEM_MAP.put("00083033", "어종 및 어기");
        EVALUATION_ITEM_MAP.put("00083034", "어장의 시설현황");
        EVALUATION_ITEM_MAP.put("00083035", "어획고 및 동변천상황과 판로");
        EVALUATION_ITEM_MAP.put("00083036", "경영상황");

        // 감정평가 테이블 유형 코드 매핑
        EVALUATION_TABLE_TYPE_MAP = new HashMap<>();
        EVALUATION_TABLE_TYPE_MAP.put("00082001", "토지감정요항표");
        EVALUATION_TABLE_TYPE_MAP.put("00082002", "건물감정평가요항표");
        EVALUATION_TABLE_TYPE_MAP.put("00082003", "구분건물감정평가요항표");
        EVALUATION_TABLE_TYPE_MAP.put("00082004", "공장감정평가요항표");
        EVALUATION_TABLE_TYPE_MAP.put("00082005", "자동차감정평가요항표");
        EVALUATION_TABLE_TYPE_MAP.put("00082006", "임야감정평가요항표");
        EVALUATION_TABLE_TYPE_MAP.put("00082007", "어업권감정평가요항표");

        // 경매 물건 용도 코드 매핑
        AUCTION_GOODS_USAGE_MAP = new HashMap<>();
        AUCTION_GOODS_USAGE_MAP.put("01", "아파트");
        AUCTION_GOODS_USAGE_MAP.put("02", "단독주택,다가구주택");
        AUCTION_GOODS_USAGE_MAP.put("03", "연립주택,다세대,빌라");
        AUCTION_GOODS_USAGE_MAP.put("04", "자동차,중기");
        AUCTION_GOODS_USAGE_MAP.put("05", "대지,임야,전답");
        AUCTION_GOODS_USAGE_MAP.put("06", "상가,오피스텔,근린시설");
        AUCTION_GOODS_USAGE_MAP.put("07", "대지");
        AUCTION_GOODS_USAGE_MAP.put("08", "임야");
        AUCTION_GOODS_USAGE_MAP.put("09", "전답");
        AUCTION_GOODS_USAGE_MAP.put("10", "단독주택");
        AUCTION_GOODS_USAGE_MAP.put("11", "다가구주택");
        AUCTION_GOODS_USAGE_MAP.put("12", "연립주택");
        AUCTION_GOODS_USAGE_MAP.put("13", "다세대");
        AUCTION_GOODS_USAGE_MAP.put("14", "빌라");
        AUCTION_GOODS_USAGE_MAP.put("15", "상가");
        AUCTION_GOODS_USAGE_MAP.put("16", "오피스텔");
        AUCTION_GOODS_USAGE_MAP.put("17", "근린시설");
        AUCTION_GOODS_USAGE_MAP.put("18", "자동차");
        AUCTION_GOODS_USAGE_MAP.put("19", "중기");
        AUCTION_GOODS_USAGE_MAP.put("99", "기타");
    }

    public static String getAuctionKindDescription(String kindCode) {
        if (kindCode == null) {
            return null;
        }
        return AUCTION_KIND_MAP.getOrDefault(kindCode, "알 수 없는 경매 종류 코드: " + kindCode);
    }

    public static String getAuctionResultDescription(String resultCode) {
        if (resultCode == null) {
            return null;
        }
        return AUCTION_RESULT_MAP.getOrDefault(resultCode, "알 수 없는 경매 결과 코드: " + resultCode);
    }

    public static String getEvaluationItemDescription(String itemCode) {
        if (itemCode == null) {
            return null;
        }
        return EVALUATION_ITEM_MAP.getOrDefault(itemCode, "알 수 없는 감정평가 요항 코드: " + itemCode);
    }

    public static String getEvaluationTableTypeDescription(String tableTypeCode) {
        if (tableTypeCode == null) {
            return null;
        }
        return EVALUATION_TABLE_TYPE_MAP.getOrDefault(tableTypeCode, "알 수 없는 감정평가 테이블 유형 코드: " + tableTypeCode);
    }

    public static String getAuctionGoodsUsageDescription(String usageCode) {
        if (usageCode == null) {
            return null;
        }
        return AUCTION_GOODS_USAGE_MAP.getOrDefault(usageCode, "알 수 없는 경매 물건 용도 코드: " + usageCode);
    }
}