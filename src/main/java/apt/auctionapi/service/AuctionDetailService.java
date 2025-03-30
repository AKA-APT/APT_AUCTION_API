package apt.auctionapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.AuctionCodeMapper;
import apt.auctionapi.entity.auction.AuctionSummary;
import apt.auctionapi.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionDetailService {

    private final AuctionRepository auctionRepository;

    public Auction getAuctionById(String id) {
        Auction auction = auctionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Auction not found"));

        // 코드값 매핑
        auction.getAuctionScheduleList().forEach(auctionSchedule -> {
            auctionSchedule.setAuctionKind(
                AuctionCodeMapper.getAuctionKindDescription(auctionSchedule.getAuctionKindCode()));
            auctionSchedule.setAuctionResult(
                AuctionCodeMapper.getAuctionResultDescription(auctionSchedule.getAuctionResultCode()));
        });

        // 코드값 매핑
        auction.getEvaluationList().forEach(auctionEvaluation -> {
            auctionEvaluation.setEvaluationCategory(
                AuctionCodeMapper.getEvaluationTableTypeDescription(auctionEvaluation.getEvaluationCategoryCode()));
            auctionEvaluation.setEvaluationItem(
                AuctionCodeMapper.getEvaluationItemDescription(auctionEvaluation.getEvaluationItemCode()));
        });

        // 코드값 매핑
        String usageCode = auction.getDisposalGoodsExecutionInfo().getAuctionGoodsUsageCode();
        auction.getDisposalGoodsExecutionInfo()
            .setAuctionGoodsUsage(AuctionCodeMapper.getAuctionGoodsUsageDescription(usageCode));

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

    public Integer getRuptureCount(AuctionSummary auctionSummary) {
        Auction auction = auctionRepository.findById(auctionSummary.getId())
            .orElse(null);
        return getRuptureCount(auction);
    }

    public List<InvestmentTag> getInvestmentTags(AuctionSummary auctionSummary) {
        Auction auction = auctionRepository.findById(auctionSummary.getId())
            .orElse(null);
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
