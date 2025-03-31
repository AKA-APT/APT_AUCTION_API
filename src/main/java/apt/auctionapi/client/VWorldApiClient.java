package apt.auctionapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import apt.auctionapi.client.dto.VWorldResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VWorldApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${VWORLD_API_URL}")
    private String vworldApiUrl;

    @Value("${DOMAIN}")
    private String domain;

    @Value("${VWORLD_API_KEY}")
    private String apiKey;

    public VWorldResponse getLocationInfo(String point) {
        // 1. 시도 정보 조회
        JsonNode sidoResult = getFeatureResult(String.format(
            "%s?service=data&request=GetFeature&data=LT_C_ADSIDO_INFO&key=%s&geomFilter=%s&format=json&domain=%s&geometry=false",
            vworldApiUrl, apiKey, point, domain));
        if (sidoResult == null) {
            return null;
        }
        String sido = sidoResult.path("properties").path("ctp_kor_nm").asText();
        String sidoCode = sidoResult.path("properties").path("ctprvn_cd").asText();

        // 2. 시군구 정보 조회
        JsonNode sigunguResult = getFeatureResult(String.format(
            "%s?service=data&request=GetFeature&data=LT_C_ADSIGG_INFO&key=%s&geomFilter=%s&format=json&domain=%s&geometry=false",
            vworldApiUrl, apiKey, point, domain));
        if (sigunguResult == null) {
            return null;
        }
        String sigungu = sigunguResult.path("properties").path("sig_kor_nm").asText();
        String sigunguCode = sigunguResult.path("properties").path("sig_cd").asText();

        // 3. 읍면동 정보 조회
        JsonNode dongResult = getFeatureResult(String.format(
            "%s?service=data&request=GetFeature&data=LT_C_ADEMD_INFO&key=%s&geomFilter=%s&format=json&domain=%s&geometry=false",
            vworldApiUrl, apiKey, point, domain));
        if (dongResult == null) {
            return null;
        }
        String dong = dongResult.path("properties").path("emd_kor_nm").asText();

        return new VWorldResponse(sido, sidoCode, sigungu, sigunguCode, dong);
    }

    private JsonNode getFeatureResult(String url) {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String responseBody = response.getBody();

        if (responseBody == null || responseBody.trim().isEmpty()) {
            return null;
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode responseNode = root.path("response");

            String status = responseNode.path("status").asText();

            if (!"OK".equals(status)) {
                return null;
            }

            JsonNode features = responseNode.path("result")
                .path("featureCollection")
                .path("features");

            if (!features.isArray() || features.isEmpty()) {
                return null;
            }

            return features.get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
