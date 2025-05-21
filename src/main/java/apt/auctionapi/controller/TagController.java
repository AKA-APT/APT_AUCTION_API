package apt.auctionapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apt.auctionapi.auth.AuthMember;
import apt.auctionapi.controller.dto.response.InvestmentTagResponse;
import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.Member;
import apt.auctionapi.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "투자 유형 태그", description = "사용자 투자 유형 태그 조회 및 변경 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/me/investment-tags")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "개인 투자 유형 태그 변경", description = "현재 로그인한 사용자의 투자 유형 태그 목록을 변경합니다.")
    @PutMapping
    public ResponseEntity<List<InvestmentTagResponse>> updateAuctionInvestmentTags(
        @AuthMember Member member,
        @RequestBody List<Integer> tagIds
    ) {
        List<InvestmentTag> savedTags = tagService.updateInvestmentTagsForMember(member, tagIds);

        List<InvestmentTagResponse> response = savedTags.stream()
            .map(tag -> new InvestmentTagResponse(tag.getId(), tag.getName(), tag.getDescription()))
            .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "개인 투자 유형 태그 조회", description = "현재 로그인한 사용자의 투자 유형 태그 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<InvestmentTagResponse>> getMemberInvestmentTags(@AuthMember Member member) {
        List<InvestmentTag> investmentTags = tagService.getInvestmentTagsForMember(member);

        List<InvestmentTagResponse> response = investmentTags.stream()
            .map(tag -> new InvestmentTagResponse(tag.getId(), tag.getName(), tag.getDescription()))
            .toList();

        return ResponseEntity.ok(response);
    }
}
