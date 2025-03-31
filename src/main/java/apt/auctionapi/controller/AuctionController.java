package apt.auctionapi.controller;

import java.util.Arrays;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apt.auctionapi.auth.AuthMember;
import apt.auctionapi.controller.dto.request.AuctionSearchRequest;
import apt.auctionapi.controller.dto.response.AuctionSummaryGroupedResponse;
import apt.auctionapi.controller.dto.response.InvestmentTagResponse;
import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.auction.Auction;
import apt.auctionapi.entity.auction.AuctionSummary;
import apt.auctionapi.service.AuctionService;
import apt.auctionapi.service.InterestService;
import apt.auctionapi.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "경매", description = "경매 목록 조회 및 상세 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final InterestService interestService;
    private final SearchService searchService;

    @Operation(summary = "경매 목록 조회", description = "지정한 범위 내의 경매 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AuctionSummaryGroupedResponse>> getAuctionsByLocation(
        @ParameterObject @ModelAttribute AuctionSearchRequest filter,
        @AuthMember(required = false) Member member
    ) {
        return ResponseEntity.ok(
            searchService.getAuctionsByLocationRange(filter, member)
        );
    }

    @Operation(summary = "경매 상세 조회", description = "지정한 경매 ID에 해당하는 경매의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable String id) {
        return ResponseEntity.ok(auctionService.getAuctionById(id));
    }

    @Operation(summary = "좋아요", description = "사용자가 경매를 좋아요합니다.")
    @PutMapping("/interests/{id}")
    public ResponseEntity<Void> interestAuction(
        @AuthMember Member member,
        @Schema(description = "경매 ID", example = "60f1b3b3b3b3b3b3b3b3b3b3")
        @PathVariable String id
    ) {
        interestService.interestAuction(member, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요한 목록", description = "사용자가 좋아요한 경매 목록을 조회합니다.")
    @GetMapping("/interests")
    public ResponseEntity<List<AuctionSummary>> getInterestAuctions(
        @AuthMember Member member
    ) {
        return ResponseEntity.ok(interestService.getInterestedAuctions(member));
    }

    @Operation(summary = "경매 투자 유형 태그 조회", description = "지정한 경매 ID에 해당하는 경매의 투자 유형 태그를 조회합니다.")
    @GetMapping("/{id}/investment-tags")
    public ResponseEntity<List<InvestmentTagResponse>> getAuctionInvestmentTags(
        @Schema(description = "경매 ID", example = "60f1b3b3b3b3b3b3b3b3b3b3")
        @PathVariable String id
    ) {
        List<InvestmentTag> tags = auctionService.getInvestmentTagsForAuction(id);

        List<InvestmentTagResponse> response = tags.stream()
            .map(tag -> new InvestmentTagResponse(tag.getId(), tag.getName(), tag.getDescription()))
            .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 투자 유형 태그 조회", description = "시스템에서 사용 가능한 모든 투자 유형 태그를 조회합니다.")
    @GetMapping("/investment-tags")
    public ResponseEntity<List<InvestmentTagResponse>> getAllInvestmentTags() {
        List<InvestmentTagResponse> response = Arrays.stream(InvestmentTag.values())
            .map(tag -> new InvestmentTagResponse(tag.getId(), tag.getName(), tag.getDescription()))
            .toList();

        return ResponseEntity.ok(response);
    }
}
