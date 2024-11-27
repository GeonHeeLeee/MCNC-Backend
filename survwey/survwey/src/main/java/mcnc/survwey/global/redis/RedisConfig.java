package mcnc.survwey.global.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.mail.service.MailService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final MailService mailService;

    @Value("${SURVEY_VERIFY_URL}")
    private String notificationUrl;

    /**
     * 설문 결과 알림
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener((message, pattern) -> {
            String expiredKey = message.toString();
            if (expiredKey.startsWith("survey:end:")) {
                Long surveyId = Long.parseLong(expiredKey.split(":")[2]);
                //여기에 이메일 전송 로직
                log.info("redis 이메일 전송: {}", surveyId);
//                mailService.sendVerifySurveyLink(userId, surveyId, notificationUrl);
            }
        }, new PatternTopic("__keyevent@*__:expired"));

        return container;
    }
}
