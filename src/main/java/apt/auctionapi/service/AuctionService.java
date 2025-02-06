package apt.auctionapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import apt.auctionapi.entity.Auction;
import apt.auctionapi.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public List<Auction> findAuctionsWithinBounds(double minLat, double minLon, double maxLat, double maxLon) {
        return auctionRepository.findAuctionsWithinBounds(minLat, minLon, maxLat, maxLon);
    }
}