package apt.auctionapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tag;

public interface InvestmentTagRepository extends JpaRepository<Tag, Long> {

    void saveAll(List<Tag> memberInvestmentTags);

    void deleteByMember(Member member);
}
