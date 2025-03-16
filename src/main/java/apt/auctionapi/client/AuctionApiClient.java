package apt.auctionapi.client;

import apt.auctionapi.controller.dto.response.AuctionStatusResponse;
import apt.auctionapi.entity.auction.Auction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class AuctionApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String COURT_API_URL = "https://www.courtauction.go.kr/pgj/pgj15A/selectAuctnCsSrchRslt.on";

    /**
     * 법원 API 호출하여 최신 경매 상태 가져오기
     */
    public AuctionStatusResponse fetchLatestAuctionStatus(Auction auction) {
        try {
            // 요청 바디 구성
            String requestBody = """
                {
                    "dma_srchCsDtlInf": {
                        "cortOfcCd": "%s",
                        "csNo": "%s"
                    }
                }
                """.formatted(auction.getCaseBaseInfo().getCourtCode(), auction.getCaseBaseInfo().getUserCaseNumber());

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SC-Pgmid", "PGJ15AF01");
            headers.add("SC-Userid", "NONUSER");
            headers.add(HttpHeaders.REFERER,
                    "https://www.courtauction.go.kr/pgj/index.on?w2xPath=/pgj/ui/pgj100/PGJ159M00.xml");
            headers.add(HttpHeaders.USER_AGENT,
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // API 요청 실행
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    COURT_API_URL, HttpMethod.POST, requestEntity, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // 응답 JSON 파싱
                JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
                JsonNode disposalList = rootNode.path("data").path("dlt_dspslGdsDspslObjctLst");

                // MongoDB에서 가져온 disposalGoodsSequence 값
                int targetSeq = auction.getDisposalGoodsExecutionInfo().getDisposalGoodsSequence();

                // 해당하는 dspslGdsSeq 값을 찾기
                for (JsonNode disposalItem : disposalList) {
                    int dspslGdsSeq = disposalItem.path("dspslGdsSeq").asInt();
                    if (dspslGdsSeq == targetSeq) {
                        // 해당하는 아이템에서 필요한 정보 추출
                        String auctionStatus = mapAuctionStatus(disposalItem.path("auctnGdsStatCd").asText());
                        String auctionDate = disposalItem.path("dspslDxdyYmd").asText();
                        Long auctionPrice = disposalItem.path("dspslAmt").asLong();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                        return new AuctionStatusResponse(auctionStatus, LocalDate.parse(auctionDate, formatter),
                                auctionPrice,
                                "유찰".equals(auctionStatus) ? getRuptureCount(auction) + 1 : getRuptureCount(auction));
                    }
                }
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * `auctnSuspStatCd` 또는 `auctnGdsStatCd` 값을 의미 있는 값으로 변환
     */
    private String mapAuctionStatus(String code) {
        return switch (code) {
            case "03" -> "유찰";
            case "04" -> "낙찰";
            default -> "신규";
        };
    }

    private Integer getRuptureCount(Auction auction) {
        if (auction == null || auction.getAuctionScheduleList() == null) {
            return 0; // auction 또는 일정 리스트가 없으면 0 반환
        }

        return (int)auction.getAuctionScheduleList().stream()
                .filter(schedule -> "002".equals(schedule.getAuctionResultCode())) // 유찰 코드 필터링
                .count();
    }
}
