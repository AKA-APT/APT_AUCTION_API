package apt.auctionapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import apt.auctionapi.entity.Interest;
import apt.auctionapi.entity.Member;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    List<Interest> findAllByMemberId(Long memberId);

    Optional<Interest> findByMemberAndAuctionId(Member member, String auctionId);
}
