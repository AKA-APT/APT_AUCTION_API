package apt.auctionapi.controller;

import apt.auctionapi.auth.AuthMember;
import apt.auctionapi.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    // 인증이 필요한 API
    @GetMapping("/me")
    public ResponseEntity<Member> getMyInfo(@AuthMember Member member) {
        return ResponseEntity.ok(member);
    }
}
