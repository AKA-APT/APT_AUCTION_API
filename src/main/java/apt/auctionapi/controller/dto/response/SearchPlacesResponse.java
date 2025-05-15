package apt.auctionapi.controller.dto.response;

import java.util.List;

import apt.auctionapi.entity.Place;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SearchPlacesResponse(
    Integer totalCount,
    List<InnerPlaceResponse> places
) {

    @Builder(access = AccessLevel.PRIVATE)
    public record InnerPlaceResponse(
        String id,
        String placeName,
        String categoryName,
        String categoryGroupCode,
        String categoryGroupName,
        String phone,
        String addressName,
        String roadAddressName,
        String longitude,
        String latitude,
        String placeUrl,
        String distance
    ) {
        public static InnerPlaceResponse from(Place p) {
            return InnerPlaceResponse.builder()
                .id(p.getId())
                .placeName(p.getPlaceName())
                .categoryName(p.getCategoryName())
                .categoryGroupCode(p.getCategoryGroupCode())
                .categoryGroupName(p.getCategoryGroupName())
                .phone(p.getPhone())
                .addressName(p.getAddressName())
                .roadAddressName(p.getRoadAddressName())
                .longitude(p.getX())
                .latitude(p.getY())
                .placeUrl(p.getPlaceUrl())
                .distance(p.getDistance())
                .build();
        }
    }

    public static SearchPlacesResponse of(List<Place> places) {
        List<InnerPlaceResponse> converted = places.stream()
            .map(InnerPlaceResponse::from)
            .toList();

        return SearchPlacesResponse.builder()
            .totalCount(converted.size())
            .places(converted)
            .build();
    }
}
