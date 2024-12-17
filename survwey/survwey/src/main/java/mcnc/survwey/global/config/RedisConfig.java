package mcnc.survwey.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.mail.service.MailService;
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

    @Value("${NOTIFICATION_URL}")
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
                String key = expiredKey.split(":")[2];
                String userId = key.split("/")[0];
                Long surveyId = Long.parseLong(key.split("/")[1]);
                //이메일 전송
                mailService.sendExpiredNotificationLink(userId, surveyId, notificationUrl);
            }
        }, new PatternTopic("__keyevent@*__:expired"));

        return container;
    }
}
