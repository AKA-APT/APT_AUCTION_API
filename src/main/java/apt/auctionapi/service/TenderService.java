package apt.auctionapi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.controller.dto.request.CreateTenderRequest;
import apt.auctionapi.controller.dto.response.AuctionStatusResponse;
import apt.auctionapi.controller.dto.response.TenderResponse;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tender;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.repository.AuctionRepository;
import apt.auctionapi.repository.TenderRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TenderService {

    private final AuctionRepository auctionRepository;
    private final TenderRepository tenderRepository;

    public void createTender(Member member, CreateTenderRequest request) {
        Auction auction = auctionRepository.findById(request.auctionId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매물입니다 " + request.auctionId()));
        BigDecimal biddingPrice = auction.getLatestBiddingPrice();
        if (request.amount() < biddingPrice.longValue()) {
            throw new IllegalArgumentException("최저 입찰가보다 낮은 금액으로 입찰할 수 없습니다");
        }
        tenderRepository.save(
            Tender.builder()
                .auctionId(auction.getId())
                .amount(request.amount())
                .member(member)
                .build()
        );
    }

    @Transactional(readOnly = true)
    public List<TenderResponse> getTender(Member member) {
        List<Tender> tenders = tenderRepository.findAllByMemberId(member.getId());
        List<TenderResponse> result = new ArrayList<>();
        for (Tender tender : tenders) {
            Auction auction = auctionRepository.findById(tender.getAuctionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매물입니다 " + tender.getAuctionId()));
            result.add(
                new TenderResponse(
                    auction.getId(),
                    auction,
                    tender.getAmount(),
                    AuctionStatusResponse.from(auction)
                )
            );
        }
        return result;
    }
}
