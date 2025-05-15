package apt.auctionapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import apt.auctionapi.controller.dto.response.SearchPlacesResponse;
import apt.auctionapi.entity.CategoryGroupCode;
import apt.auctionapi.entity.Place;
import apt.auctionapi.service.KakaoPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@Tag(name = "편의시설", description = "주변 편의시설 조회 API")
@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Validated
public class KakaoPlaceController {

    private final KakaoPlaceService kakaoPlaceService;

    @Operation(
        summary = "좌표로 반경 편의시설 정보 조회",
        description = "위도와 경도 좌표를 기반으로 반경 편의시설 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<SearchPlacesResponse> search(
        @Parameter(
            description = """
                    카테고리 코드 목록 (복수 선택 가능)
                    - MT1: 대형마트
                    - CS2: 편의점
                    - PS3: 어린이집, 유치원
                    - SC4: 학교
                    - AC5: 학원
                    - PK6: 주차장
                    - OL7: 주유소, 충전소
                    - SW8: 지하철역
                    - BK9: 은행
                    - CT1: 문화시설
                    - AG2: 중개업소
                    - PO3: 공공기관
                    - AT4: 관광명소
                    - AD5: 숙박
                    - FD6: 음식점
                    - CE7: 카페
                    - HP8: 병원
                    - PM9: 약국
                """,
            example = "FD6,CE7"
        )
        @RequestParam @NotEmpty(message = "카테고리 코드는 하나 이상 선택해야 합니다.")
        List<CategoryGroupCode> categories,

        @Parameter(description = "경도 (longitude)", example = "127.027610")
        @RequestParam @Min(value = -180, message = "경도는 -180 이상이어야 합니다.")
        @Max(value = 180, message = "경도는 180 이하이어야 합니다.")
        double longitude,

        @Parameter(description = "위도 (latitude)", example = "37.497942")
        @RequestParam @Min(value = -90, message = "위도는 -90 이상이어야 합니다.")
        @Max(value = 90, message = "위도는 90 이하이어야 합니다.")
        double latitude,

        @Parameter(description = "반경 (미터, 최대 20000)", example = "1000")
        @RequestParam @Min(value = 1, message = "반경은 최소 1m 이상이어야 합니다.")
        @Max(value = 20000, message = "반경은 최대 20000m 이하여야 합니다.")
        int radius
    ) {
        List<Place> places = kakaoPlaceService.searchPlaces(categories, longitude, latitude, radius);
        return ResponseEntity.ok(SearchPlacesResponse.of(places));
    }
}
