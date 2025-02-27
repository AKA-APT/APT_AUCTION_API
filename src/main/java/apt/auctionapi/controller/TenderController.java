package apt.auctionapi.controller;

import apt.auctionapi.auth.AuthMember;
import apt.auctionapi.controller.dto.request.CreateTenderRequest;
import apt.auctionapi.controller.dto.response.TenderResponse;
import apt.auctionapi.entity.Member;
import apt.auctionapi.service.TenderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "입찰", description = "입찰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenders")
public class TenderController {

    private final TenderService tenderService;

    @Tag(name = "입찰", description = "입찰 생성 API")
    @PostMapping
    public ResponseEntity<Void> createTender(
            @AuthMember Member member,
            @RequestBody CreateTenderRequest request
    ) {
        tenderService.createTender(member, request);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "입찰", description = "입찰 목록 조회 API")
    @GetMapping
    public ResponseEntity<List<TenderResponse>> getTenders(
            @AuthMember Member member
    ) {
        var result = tenderService.getTender(member);
        return ResponseEntity.ok(result);
    }
}
