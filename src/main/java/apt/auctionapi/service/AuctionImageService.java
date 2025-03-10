package apt.auctionapi.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.repository.AuctionRepository;

@Service
public class AuctionImageService {

    private final RestTemplate restTemplate;
    private final AuctionRepository auctionRepository;
    private final ObjectMapper objectMapper;

    public AuctionImageService(RestTemplate restTemplate, AuctionRepository auctionRepository,
        ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.auctionRepository = auctionRepository;
        this.objectMapper = objectMapper;
    }

    public JsonNode getAuctionImages(String id) {
        // MongoDB에서 해당 ID를 조회
        Optional<Auction> auctionOptional = auctionRepository.findById(id);
        if (auctionOptional.isEmpty()) {
            throw new RuntimeException("해당 ID에 대한 데이터가 존재하지 않습니다.");
        }
        Auction auction = auctionOptional.get();

        // 요청 바디 데이터 구성
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> dma_srchGdsDtlSrch = new HashMap<>();
        dma_srchGdsDtlSrch.put("csNo", auction.getCaseBaseInfo().getUserCaseNumber());
        dma_srchGdsDtlSrch.put("cortOfcCd", auction.getCaseBaseInfo().getCourtCode());
        dma_srchGdsDtlSrch.put("dspslGdsSeq", auction.getDisposalGoodsExecutionInfo().getDisposalGoodsSequence());
        dma_srchGdsDtlSrch.put("pgmId", "PGJ151F01");

        Map<String, Object> srchInfo = new HashMap<>();
        srchInfo.put("bidBgngYmd", "20250309");
        srchInfo.put("bidEndYmd", "20250323");
        srchInfo.put("sideDvsCd", "2");
        srchInfo.put("menuNm", "물건상세검색");

        dma_srchGdsDtlSrch.put("srchInfo", srchInfo);
        requestBody.put("dma_srchGdsDtlSrch", dma_srchGdsDtlSrch);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("sc-pgmid", "PGJ15BM01");
        headers.add("sc-userid", "NONUSER");
        headers.add(HttpHeaders.REFERER,
            "https://www.courtauction.go.kr/pgj/index.on?w2xPath=/pgj/ui/pgj100/PGJ151F00.xml");
        headers.add(HttpHeaders.USER_AGENT,
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // 요청 실행
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "https://www.courtauction.go.kr/pgj/pgj15B/selectAuctnCsSrchRslt.on",
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // 응답 확인
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return extractCsPicLst(responseEntity.getBody());
        } else {
            throw new RuntimeException("요청 실패: " + responseEntity.getStatusCode());
        }
    }

    /**
     * 응답 JSON에서 csPicLst 필드만 추출하는 메서드
     */
    private JsonNode extractCsPicLst(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode csPicLst = rootNode.path("data").path("dma_result").path("csPicLst");
            return csPicLst;
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 오류", e);
        }
    }
}
