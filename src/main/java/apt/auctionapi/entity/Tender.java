package apt.auctionapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 입찰에 대한 엔티티
 */
@Table(name = "tender")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tender extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auction_id")
    private String auctionId;

    @Column(name = "amount")
    private Long amount;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    private Tender(String auctionId, Long amount, Member member) {
        this.auctionId = auctionId;
        this.amount = amount;
        this.member = member;
    }
}
