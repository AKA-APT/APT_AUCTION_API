package apt.auctionapi.domain;

import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Role;

import java.io.Serializable;

public record SessionUser(
        Long id,
        String nickname,
        Role role
) implements Serializable {
    public static SessionUser from(Member member) {
        return new SessionUser(
                member.getId(),
                member.getNickname(),
                member.getRole()
        );
    }
}
