package apt.auctionapi.repository;

import apt.auctionapi.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    List<Interest> findAllByMemberId(Long memberId);
}
