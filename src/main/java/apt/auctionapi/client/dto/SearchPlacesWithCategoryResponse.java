package apt.auctionapi.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import apt.auctionapi.entity.Place;

public record SearchPlacesWithCategoryResponse(
    @JsonProperty("documents") List<Document> documents
) {

    public record Document(
        String id,
        @JsonProperty("place_name") String placeName,
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("category_group_code") String categoryGroupCode,
        @JsonProperty("category_group_name") String categoryGroupName,
        String phone,
        @JsonProperty("address_name") String addressName,
        @JsonProperty("road_address_name") String roadAddressName,
        String x,
        String y,
        @JsonProperty("place_url") String placeUrl,
        String distance
    ) {

        public Place toEntity() {
            return new Place(
                id,
                placeName,
                categoryName,
                categoryGroupCode,
                categoryGroupName,
                phone,
                addressName,
                roadAddressName,
                x,
                y,
                placeUrl,
                distance
            );
        }
    }
}
