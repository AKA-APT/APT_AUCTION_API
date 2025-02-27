package apt.auctionapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;


@Entity
@Getter
@Table(name = "interest")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "auction_id")
    private String auctionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Interest(String auctionId, Member member) {
        this.auctionId = auctionId;
        this.member = member;
    }
}
