package apt.auctionapi.service;

import apt.auctionapi.controller.dto.request.AuctionsResponse;
import apt.auctionapi.entity.Auction;
import apt.auctionapi.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public List<AuctionsResponse> findAuctionsWithinBounds(
            Double lbLat,
            Double lbLon,
            Double rtLat,
            Double rtLon
    ) {
        List<Auction> auctionsWithinBounds = auctionRepository.findAuctionsWithinBounds(lbLat, lbLon, rtLat, rtLon);
        return AuctionsResponse.from(auctionsWithinBounds);


    }
}
