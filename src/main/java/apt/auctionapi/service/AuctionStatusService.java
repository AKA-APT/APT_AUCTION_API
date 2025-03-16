package apt.auctionapi.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import apt.auctionapi.client.AuctionApiClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import apt.auctionapi.controller.dto.response.AuctionStatusResponse;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionStatusService {

    private final AuctionRepository auctionRepository;
    private final AuctionApiClient auctionApiClient;

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
        return auctionApiClient.fetchLatestAuctionStatus(auction);
    }
}
