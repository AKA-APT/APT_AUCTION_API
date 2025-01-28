package apt.auctionapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickname;
    private String profileImage;
    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Builder
    private Member(String nickname, String profileImage, String providerId) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.providerId = providerId;
    }

    public void updateProfile(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
