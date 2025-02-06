package apt.auctionapi.controller;

import apt.auctionapi.auth.AuthMember;
import apt.auctionapi.controller.dto.request.CreateTenderRequest;
import apt.auctionapi.controller.dto.response.TenderResponse;
import apt.auctionapi.entity.Member;
import apt.auctionapi.service.TenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenders")
public class TenderController {

    private final TenderService tenderService;

    // TODO: 최저 입찰가보다 낮으면 예외
    @PostMapping
    public ResponseEntity<Void> createTender(
            @AuthMember Member member,
            @RequestBody CreateTenderRequest request
    ) {
        tenderService.createTender(member, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<TenderResponse>> getTenders(
            @AuthMember Member member
    ) {
        var result = tenderService.getTender(member);
        return ResponseEntity.ok(result);
    }
}
