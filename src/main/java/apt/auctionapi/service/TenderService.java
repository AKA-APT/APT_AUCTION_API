package apt.auctionapi.service;

import apt.auctionapi.controller.dto.response.AuctionResponse;
import apt.auctionapi.controller.dto.request.CreateTenderRequest;
import apt.auctionapi.controller.dto.response.TenderResponse;
import apt.auctionapi.entity.Auction;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tender;
import apt.auctionapi.repository.AuctionRepository;
import apt.auctionapi.repository.TenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TenderService {

    private final AuctionRepository auctionRepository;
    private final TenderRepository tenderRepository;

    public void createTender(Member member, CreateTenderRequest request) {
        Auction auction = auctionRepository.findById(request.auctionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매물입니다 " + request.auctionId()));
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
                            AuctionResponse.from(auction),
                            tender.getAmount()
                    )
            );
        }
        return result;
    }
}
