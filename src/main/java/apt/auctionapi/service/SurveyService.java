package apt.auctionapi.service;

import org.springframework.stereotype.Service;

import apt.auctionapi.entity.Survey;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.repository.AuctionRepository;
import apt.auctionapi.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final AuctionRepository auctionRepository;

    public Survey getSurveyById(String id) {
        Auction auction = auctionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경매 id 입니다."));
        String courtCode = auction.getCaseBaseInfo().getCourtCode();
        String caseNumber = auction.getCaseBaseInfo().getUserCaseNumber();
        return surveyRepository.findByCourtCodeAndUserCaseNo(courtCode, caseNumber)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경매 id 입니다."));
    }
}