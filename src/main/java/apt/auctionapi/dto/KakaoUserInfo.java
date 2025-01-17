package apt.auctionapi.dto;

public record KakaoUserInfo(
        Long id,
        KakaoAccount kakao_account
) {
    public record KakaoAccount(
            Profile profile
    ) {
        public record Profile(
                String nickname,
                String profile_image_url
        ) {}
    }
}
