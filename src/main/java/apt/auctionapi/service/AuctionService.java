package apt.auctionapi.service;

import apt.auctionapi.controller.dto.request.AuctionResponse;
import apt.auctionapi.entity.Auction;
import apt.auctionapi.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    @Transactional(readOnly = true)
    public List<AuctionResponse> findAuctionsWithinBounds(
            Double lbLat,
            Double lbLon,
            Double rtLat,
            Double rtLon
    ) {
        List<Auction> auctionsWithinBounds = auctionRepository.findAuctionsWithinBounds(lbLat, lbLon, rtLat, rtLon);
        return AuctionResponse.from(auctionsWithinBounds);
    }
}
