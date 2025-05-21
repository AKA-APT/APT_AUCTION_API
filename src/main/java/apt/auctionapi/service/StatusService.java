package apt.auctionapi.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import apt.auctionapi.controller.dto.response.AuctionStatusResponse;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final AuctionRepository auctionRepository;

    public AuctionStatusResponse getAuctionStatus(String id) {
        Optional<Auction> auctionOptional = auctionRepository.findById(id);
        if (auctionOptional.isEmpty()) {
            return null;
        }
        Auction auction = auctionOptional.get();
        return AuctionStatusResponse.from(auction);
    }
}