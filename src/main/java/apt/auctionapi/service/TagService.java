package apt.auctionapi.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import apt.auctionapi.domain.InvestmentTag;
import apt.auctionapi.entity.Member;
import apt.auctionapi.entity.Tag;
import apt.auctionapi.repository.InvestmentTagRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

    private final InvestmentTagRepository investmentTagRepository;

    @Transactional
    public List<InvestmentTag> updateInvestmentTagsForMember(Member member, List<Integer> tagIds) {
        if (member == null) {
            throw new SecurityException("Unauthorized user.");
        }

        // 기존 태그 삭제
        investmentTagRepository.deleteByMember(member);

        // 태그 ID 리스트를 InvestmentTag Enum으로 변환
        List<InvestmentTag> investmentTags = tagIds.stream()
            .map(id -> Arrays.stream(InvestmentTag.values())
                .filter(tag -> tag.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid tag ID: " + id)))
            .toList();

        // 새로운 태그 추가
        List<Tag> memberInvestmentTags = investmentTags.stream()
            .map(tag -> new Tag(member, tag))
            .toList();

        investmentTagRepository.saveAll(memberInvestmentTags);

        return investmentTags;
    }

    @Transactional(readOnly = true)
    public List<InvestmentTag> getInvestmentTagsForMember(Member member) {
        if (member == null) {
            throw new SecurityException("Unauthorized user.");
        }

        List<Tag> tags = investmentTagRepository.findByMember(member);
        return tags.stream()
            .map(Tag::getTag)
            .toList();
    }
}
