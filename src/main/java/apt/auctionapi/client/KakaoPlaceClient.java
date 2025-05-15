package apt.auctionapi.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import apt.auctionapi.client.dto.SearchPlacesWithCategoryResponse;
import apt.auctionapi.client.dto.SearchPlacesWithCategoryResponse.Document;
import apt.auctionapi.entity.CategoryGroupCode;
import apt.auctionapi.entity.Place;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoPlaceClient {

    private static final String API_URL = "https://dapi.kakao.com/v2/local/search/category.json";
    private static final int PAGE_SIZE = 15;

    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String apiKey;

    public List<Place> searchByCategories(
        List<CategoryGroupCode> categories,
        double x,
        double y,
        int radius
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        List<Document> docs = new ArrayList<>();
        for (CategoryGroupCode category : categories) {
            String uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("category_group_code", category.name())
                .queryParam("x", x)
                .queryParam("y", y)
                .queryParam("radius", radius)
                .queryParam("page", 1)
                .queryParam("size", PAGE_SIZE)
                .toUriString();

            ResponseEntity<SearchPlacesWithCategoryResponse> resp = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                SearchPlacesWithCategoryResponse.class
            );
            log.info("Search response: {}", resp.getBody());
            docs.addAll(resp.getBody().documents());
        }

        return docs
            .stream()
            .map(Document::toEntity)
            .toList();
    }
}
