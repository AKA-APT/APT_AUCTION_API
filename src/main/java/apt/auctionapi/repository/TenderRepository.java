package apt.auctionapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import apt.auctionapi.entity.Tender;

public interface TenderRepository extends JpaRepository<Tender, Long> {

    List<Tender> findAllByMemberId(Long memberId);
}
