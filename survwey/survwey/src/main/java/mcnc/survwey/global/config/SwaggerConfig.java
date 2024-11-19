package mcnc.survwey.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springSurvweyOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("MCNC - Survwey API")
                        .description("모빌씨앤씨 인턴쉽 프로젝트 API 명세서<br>" +
                                "서버에서 핸들링한 에러 대부분은 key: errorMessage, value: 해당 아이디의 사용자가 존재하지 않습니다. 형태(key 고정)<br>" +
                                "ex) errorMessage : 해당 아이디의 사용자가 존재하지 않습니다. ")
                        .version("v1.0"));
    }
}