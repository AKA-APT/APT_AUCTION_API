package apt.auctionapi.service;

import static apt.auctionapi.entity.OAuthProvider.KAKAO;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import apt.auctionapi.auth.dto.KakaoUserInfo;
import apt.auctionapi.auth.dto.OAuthTokenDto;
import apt.auctionapi.config.KakaoOAuthConfig;
import apt.auctionapi.domain.SessionUser;
import apt.auctionapi.entity.Member;
import apt.auctionapi.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuthService {

    private final KakaoOAuthConfig kakaoOAuthConfig;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final HttpSession httpSession;

    @Transactional
    public SessionUser kakaoLogin(String code) {
        String accessToken = getKakaoAccessToken(code);
        KakaoUserInfo userInfo = getKakaoUserInfo(accessToken);
        Member member = saveOrUpdate(userInfo);
        SessionUser sessionUser = SessionUser.from(member);
        httpSession.setAttribute("user", sessionUser);

        return sessionUser;
    }

    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthConfig.getClientId());
        params.add("redirect_uri", kakaoOAuthConfig.getRedirectUri());
        params.add("code", code);
        params.add("client_secret", kakaoOAuthConfig.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request =
            new HttpEntity<>(params, headers);

        ResponseEntity<OAuthTokenDto> response = restTemplate.postForEntity(
            kakaoOAuthConfig.getTokenUri(),
            request,
            OAuthTokenDto.class
        );

        OAuthTokenDto body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("Failed to get access token from Kakao");
        }

        return body.access_token();
    }

    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
            kakaoOAuthConfig.getUserInfoUri(),
            HttpMethod.GET,
            request,
            KakaoUserInfo.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to get user info from Kakao");
        }

        return response.getBody();
    }

    protected Member saveOrUpdate(KakaoUserInfo userInfo) {
        Member member = memberRepository.findByProviderId(String.valueOf(userInfo.id()))
            .map(entity -> {
                entity.updateProfile(
                    userInfo.kakao_account().profile().nickname(),
                    userInfo.kakao_account().profile().profile_image_url()
                );
                return entity;
            })
            .orElse(Member.builder()
                .nickname(userInfo.kakao_account().profile().nickname())
                .profileImage(userInfo.kakao_account().profile().profile_image_url())
                .providerId(String.valueOf(userInfo.id()))
                .provider(KAKAO)
                .build());

        return memberRepository.save(member);
    }
}
