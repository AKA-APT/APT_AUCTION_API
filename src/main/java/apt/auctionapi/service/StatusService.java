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

    /**
     * 특정 ID로 MongoDB에서 경매 상태 조회 및 법원 API 요청
     */
    public AuctionStatusResponse getAuctionStatus(String id) {
        // MongoDB에서 해당 ID 조회
        Optional<Auction> auctionOptional = auctionRepository.findById(id);
        if (auctionOptional.isEmpty()) {
            return null;
        }
        Auction auction = auctionOptional.get();
        return AuctionStatusResponse.from(auction);
    }
}