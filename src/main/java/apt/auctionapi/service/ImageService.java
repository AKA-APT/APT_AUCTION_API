package apt.auctionapi.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
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

import apt.auctionapi.entity.AuctionDocument;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.repository.AuctionRepository;
import apt.auctionapi.repository.AuctionImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${url.auction}")
    private String BASE_URL;

    private final RestTemplate restTemplate;
    private final AuctionRepository auctionRepository;
    private final ObjectMapper objectMapper;
    private final AuctionImageRepository auctionRepo;

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
            return rootNode.path("data").path("dma_result").path("csPicLst");
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 오류", e);
        }
    }

    public byte[] getPhotoBytes(String auctionId, int photoIndex) {
        ObjectId oid;
        try {
            oid = new ObjectId(auctionId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효한 auction_id(ObjectId) 형식이 아닙니다: " + auctionId);
        }

        AuctionDocument doc = auctionRepo.findByAuctionId(oid)
            .orElseThrow(() -> new IllegalArgumentException(
                "해당 auction_id를 찾을 수 없습니다: " + auctionId));

        if (photoIndex < 0 || photoIndex >= doc.getPhotoCount()) {
            throw new IllegalArgumentException(
                "photo_index는 0부터 " + doc.getPhotoCount() + " 사이여야 합니다.");
        }

        String url = String.format(
            "%s/%s/%s/%s/%s/%d.jpg?w=512",
            BASE_URL, doc.getCaseSite(), doc.getCaseName(), doc.getItemNumber(), "사진", photoIndex
        );

        ResponseEntity<byte[]> resp = restTemplate.getForEntity(url, byte[].class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("이미지 다운로드 실패: " + url);
        }
        return resp.getBody();
    }
}
