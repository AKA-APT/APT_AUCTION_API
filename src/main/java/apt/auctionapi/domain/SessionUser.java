package apt.auctionapi.domain;

import java.io.Serializable;

import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Role;

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
