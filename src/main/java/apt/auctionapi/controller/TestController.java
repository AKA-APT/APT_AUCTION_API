package apt.auctionapi.controller;

import apt.auctionapi.auth.AuthMember;
import apt.auctionapi.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestController {

    // 인증이 필요한 API
    @GetMapping("/test/auth")
    public ResponseEntity<Member> getMyInfo(@AuthMember Member member) {
        return ResponseEntity.ok(member);
    }

    // 인증이 필요없는 API
    @GetMapping("/test/non-auth")
    public ResponseEntity<String> getPublicInfo() {
        return ResponseEntity.ok("잘 호출되었소");
    }
}
