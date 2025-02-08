package apt.auctionapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    private final String serverUrl;

    public SwaggerConfig(
        @Value("${swagger.server-url}") String serverUrl
    ) {
        this.serverUrl = serverUrl;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(serverUrl);
        return new OpenAPI()
            .info(new Info()
                .title("Auction API")
                .version("1.0")
                .description("경매 데이터를 관리하는 API 문서"))
            .addServersItem(server);
    }
}
