package apt.auctionapi.service;

import apt.auctionapi.controller.dto.response.AuctionResponse;
import apt.auctionapi.entity.Auction;
import apt.auctionapi.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    @Transactional(readOnly = true)
    public List<AuctionResponse> findAuctionsWithinBounds(
            Double lbLat,  // 좌하단 위도
            Double lbLng,  // 좌하단 경도
            Double rtLat,  // 우상단 위도
            Double rtLng   // 우상단 경도
    ) {
        // null 값 확인
        validateCoordinatesNotNull(lbLat, lbLng, rtLat, rtLng);

        // 위도, 경도가 올바른 범위인지 확인
        validateLatitudeRange(lbLat, rtLat);
        validateLongitudeRange(lbLng, rtLng);

        // 보정된 좌표로 경매 정보 조회
        List<Auction> auctionsWithinBounds = auctionRepository.findAuctionsWithinBounds(
            lbLat, rtLat, lbLng, rtLng
        );

        // bjdInfo와 location이 존재하는 경매만 필터링
        List<Auction> result = auctionsWithinBounds.stream()
                .filter(it -> it.getBjdInfo() != null)
                .filter(it -> it.getBjdInfo().getLocation() != null)
                .toList();

        return AuctionResponse.from(result);
    }

    // 좌표값이 null인지 확인하는 메서드
    private void validateCoordinatesNotNull(Double... coordinates) {
        if (Arrays.stream(coordinates).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("좌표값은 null일 수 없습니다.");
        }
    }

    // 위도값이 올바른 범위(-90 ~ 90)인지 확인하는 메서드
    private void validateLatitudeRange(Double... latitudes) {
        for (Double lat : latitudes) {
            if (lat < -90 || lat > 90) {
                throw new IllegalArgumentException(
                        String.format("위도는 -90도에서 90도 사이여야 합니다. 현재 값: %f", lat)
                );
            }
        }
    }

    // 경도값이 올바른 범위(-180 ~ 180)인지 확인하는 메서드
    private void validateLongitudeRange(Double... longitudes) {
        for (Double lng : longitudes) {
            if (lng < -180 || lng > 180) {
                throw new IllegalArgumentException(
                        String.format("경도는 -180도에서 180도 사이여야 합니다. 현재 값: %f", lng)
                );
            }
        }
    }
}
