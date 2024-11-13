package mcnc.survwey.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springCapstoneOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("MCNC - Survwey API")
                        .description("모빌씨앤씨 인턴쉽 프로젝트 API 명세서")
                        .version("v1.0"));
    }
}