package apt.auctionapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import apt.auctionapi.client.KakaoPlaceClient;
import apt.auctionapi.entity.CategoryGroupCode;
import apt.auctionapi.entity.Place;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoPlaceService {

    private final KakaoPlaceClient kakaoPlaceClient;

    public List<Place> searchPlaces(
        List<CategoryGroupCode> categories,
        double x,
        double y,
        int radius
    ) {
        return kakaoPlaceClient.searchByCategories(categories, x, y, radius);
    }
}
