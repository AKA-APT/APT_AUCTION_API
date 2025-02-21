package apt.auctionapi.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import apt.auctionapi.entity.StringToLocalDateConverter;

@Configuration
@EnableMongoRepositories(basePackages = "apt.auctionapi.repository")
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
            new StringToLocalDateConverter() // String → LocalDate
            // new LocalDateToStringConverter()   // LocalDate → String
        ));
    }
}