package apt.auctionapi.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.AuctionDetail;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.repository.AuctionDetailRepository;
import apt.auctionapi.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionDetailRepository auctionDetailRepository;

    public Auction getAuctionById(String id) {
        Auction auction = auctionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        auction.mappingCodeValues();
        return auction;
    }

    public AuctionDetail getAuctionDetailById(String id) {
        ObjectId oid;
        try {
            oid = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효한 auction_id(ObjectId) 형식이 아닙니다: " + id);
        }
        return auctionDetailRepository.findByAuctionId(oid)
            .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
    }

    public List<InvestmentTag> getInvestmentTagsForAuction(String auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        return InvestmentTag.from(auction);
    }
}
