package apt.auctionapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tag;

public interface InvestmentTagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByMember(Member member);

    void deleteByMember(Member member);
}
