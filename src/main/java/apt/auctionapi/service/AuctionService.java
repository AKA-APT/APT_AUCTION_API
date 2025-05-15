package apt.auctionapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public Auction getAuctionById(String id) {
        Auction auction = auctionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        auction.mappingCodeValues();
        return auction;
    }

    public List<InvestmentTag> getInvestmentTagsForAuction(String auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        return InvestmentTag.from(auction);
    }
}
