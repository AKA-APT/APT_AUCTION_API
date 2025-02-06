package apt.auctionapi.repository;

import apt.auctionapi.entity.Tender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenderRepository extends JpaRepository<Tender, Long> {

    List<Tender> findAllByMemberId(Long memberId);
}
