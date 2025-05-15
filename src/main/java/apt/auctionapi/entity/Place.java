package apt.auctionapi.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Place {

    private final String id;
    private final String placeName;
    private final String categoryName;
    private final String categoryGroupCode;
    private final String categoryGroupName;
    private final String phone;
    private final String addressName;
    private final String roadAddressName;
    private final String x;
    private final String y;
    private final String placeUrl;
    private final String distance;
}
