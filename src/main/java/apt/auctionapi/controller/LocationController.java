package apt.auctionapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apt.auctionapi.controller.dto.request.LocationRequest;
import apt.auctionapi.controller.dto.response.LocationResponse;
import apt.auctionapi.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "위치 정보", description = "위치 정보 관련 API")
@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @Operation(
        summary = "좌표로 주소 정보 조회",
        description = "위도와 경도 좌표를 기반으로 시도, 시군구, 읍면동 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "위치 정보 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\n" +
                        "  \"sido\": \"서울특별시\",\n" +
                        "  \"sigungu\": \"강남구\",\n" +
                        "  \"dong\": \"삼성동\",\n" +
                        "  \"fullAddress\": \"서울특별시 강남구 삼성동\"\n" +
                        "}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "위치 정보를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "null"
                )
            )
        )
    })
    @PostMapping("/address")
    public LocationResponse getAddressFromCoordinates(
        @Parameter(
            description = "위치 정보 요청",
            required = true
        )
        @RequestBody
        @Schema(
            example = "{\n" +
                "  \"latitude\": 37.5665,\n" +
                "  \"longitude\": 126.9780\n" +
                "}"
        )
        LocationRequest request
    ) {
        return locationService.getAddressFromCoordinates(request);
    }
}
