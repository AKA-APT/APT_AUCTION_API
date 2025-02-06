package apt.auctionapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Auction API")
                .version("1.0")
                .description("경매 데이터를 관리하는 API 문서")
                .contact(new Contact()
                    .name("Support Team")
                    .email("support@example.com")
                    .url("https://example.com")));
    }
}