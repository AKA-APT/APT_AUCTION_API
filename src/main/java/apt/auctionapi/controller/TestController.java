package apt.auctionapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apt.auctionapi.auth.AuthMember;
import apt.auctionapi.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "테스트", description = "테스트 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestController {

    // 인증이 필요한 API
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원의 정보를 조회합니다.")
    @GetMapping("/test/auth")
    public ResponseEntity<Member> getMyInfo(@AuthMember Member member) {
        return ResponseEntity.ok(member);
    }

    // 인증이 필요없는 API
    @Operation(summary = "공개 정보 조회", description = "인증이 필요없는 정보를 조회합니다.")
    @GetMapping("/test/non-auth")
    public ResponseEntity<String> getPublicInfo() {
        return ResponseEntity.ok("잘 호출되었소");
    }
}
