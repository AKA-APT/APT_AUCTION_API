package apt.auctionapi.service;

import org.springframework.stereotype.Service;

import apt.auctionapi.client.VWorldApiClient;
import apt.auctionapi.client.dto.VWorldResponse;
import apt.auctionapi.controller.dto.request.LocationRequest;
import apt.auctionapi.controller.dto.response.LocationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final VWorldApiClient vWorldApiClient;

    public LocationResponse getAddressFromCoordinates(LocationRequest request) {
        String point = String.format("POINT(%f %f)", request.longitude(), request.latitude());

        VWorldResponse vWorldResponse = vWorldApiClient.getLocationInfo(point);
        if (vWorldResponse == null) {
            return null;
        }

        String fullAddress = String.format("%s %s %s",
            vWorldResponse.sido(),
            vWorldResponse.sigungu(),
            vWorldResponse.dong());

        return new LocationResponse(
            vWorldResponse.sido(),
            vWorldResponse.sigungu(),
            vWorldResponse.dong(),
            fullAddress
        );
    }
}
