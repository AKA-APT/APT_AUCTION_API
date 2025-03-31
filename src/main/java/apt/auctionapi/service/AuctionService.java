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

    public Integer getRuptureCount(Auction auction) {
        if (auction == null || auction.getAuctionScheduleList() == null) {
            return 0;
        }

        return (int)auction.getAuctionScheduleList().stream()
            .filter(schedule -> "002".equals(schedule.getAuctionResultCode()))
            .count();
    }

    public List<InvestmentTag> getInvestmentTags(Auction auction) {
        if (auction == null) {
            return List.of();
        }
        return InvestmentTag.from(auction);
    }

    public List<InvestmentTag> getInvestmentTagsForAuction(String auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        return InvestmentTag.from(auction);
    }
}
