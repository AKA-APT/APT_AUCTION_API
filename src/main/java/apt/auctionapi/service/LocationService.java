package apt.auctionapi.service;

import org.springframework.stereotype.Service;

import apt.auctionapi.client.VWorldApiClient;
import apt.auctionapi.client.dto.VWorldResponse;
import apt.auctionapi.controller.dto.request.SearchAddressRequest;
import apt.auctionapi.controller.dto.response.SearchAddressResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final VWorldApiClient vWorldApiClient;

    public SearchAddressResponse getAddressFromCoordinates(SearchAddressRequest request) {
        String point = String.format("POINT(%f %f)", request.longitude(), request.latitude());

        VWorldResponse vWorldResponse = vWorldApiClient.getLocationInfo(point);
        if (vWorldResponse == null) {
            return null;
        }

        String fullAddress = String.format("%s %s %s",
            vWorldResponse.sido(),
            vWorldResponse.sigungu(),
            vWorldResponse.dong());

        return new SearchAddressResponse(
            vWorldResponse.sido(),
            vWorldResponse.sigungu(),
            vWorldResponse.dong(),
            fullAddress
        );
    }
}
