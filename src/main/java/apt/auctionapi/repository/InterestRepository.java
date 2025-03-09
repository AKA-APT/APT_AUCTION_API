package apt.auctionapi.repository;

import apt.auctionapi.entity.Interest;
import apt.auctionapi.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    List<Interest> findAllByMemberId(Long memberId);

    Optional<Interest> findByMemberAndAuctionId(Member member, String auctionId);
}
