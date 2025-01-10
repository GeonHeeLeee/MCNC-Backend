package mcnc.survwey.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;


@Configuration
public class ThymeleafConfig {

    /**
     * 템플릿 엔진 초기화
     * @return
     * @Author 이강민
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        //템플릿 엔진에 templateResolver 설정
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }

    /**
     * 템플릿 엔진 초기화
     * @return
     * @Author 이강민
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        //기본 경로
        templateResolver.setPrefix("classpath:/templates/");
        //확장자
        templateResolver.setSuffix(".html");
        //HTML 템플릿 처리
        templateResolver.setTemplateMode("HTML");
        //인코딩 형식
        templateResolver.setCharacterEncoding("UTF-8");
        //캐싱 활성화
        templateResolver.setCacheable(true);
        return templateResolver;
    }
}