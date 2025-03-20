package apt.auctionapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apt.auctionapi.auth.AuthMember;
import apt.auctionapi.controller.dto.response.InvestmentTagResponse;
import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.Member;
import apt.auctionapi.service.InvestmentTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "경매", description = "경매 목록 조회 및 상세 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/me/Investment-tags")
public class InvestmentTagController {

    InvestmentTagService investmentTagService;

    @Operation(summary = "경매 투자 유형 태그 추가", description = "사용자가 특정 경매에 투자 유형 태그를 추가합니다.")
    @PutMapping
    public ResponseEntity<List<InvestmentTagResponse>> updateAuctionInvestmentTags(
        @AuthMember Member member,
        @RequestBody List<Integer> tagIds
    ) {
        List<InvestmentTag> savedTags = investmentTagService.updateInvestmentTagsForMember(member, tagIds);

        List<InvestmentTagResponse> response = savedTags.stream()
            .map(tag -> new InvestmentTagResponse(tag.getId(), tag.getName(), tag.getDescription()))
            .toList();

        return ResponseEntity.ok(response);
    }
}
