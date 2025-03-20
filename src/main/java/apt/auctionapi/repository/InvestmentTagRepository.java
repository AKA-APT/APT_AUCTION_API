package apt.auctionapi.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tag;

public interface InvestmentTagRepository extends Repository<Tag, Integer> {

    void saveAll(List<Tag> memberInvestmentTags);

    void deleteByMember(Member member);
}
