package apt.auctionapi.repository;

import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import apt.auctionapi.entity.Survey;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SurveyRepository {

    private final MongoTemplate mongoTemplate;

    public Optional<Survey> findByCourtCodeAndUserCaseNo(String courtOfficeCode, String userCaseNo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dma_curstExmnMngInf.cortOfcCd").is(courtOfficeCode)
            .and("dma_curstExmnMngInf.userCsNo").is(userCaseNo));

        Survey survey = mongoTemplate.findOne(query, Survey.class);
        return Optional.ofNullable(survey);
    }
}