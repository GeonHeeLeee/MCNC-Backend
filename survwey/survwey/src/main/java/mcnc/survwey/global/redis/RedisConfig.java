package mcnc.survwey.global.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {


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
                //surveyEmailService.sendEmailToSurveyParticipants(surveyId);
            }
        }, new PatternTopic("__keyevent@*__:expired"));

        return container;
    }
}
