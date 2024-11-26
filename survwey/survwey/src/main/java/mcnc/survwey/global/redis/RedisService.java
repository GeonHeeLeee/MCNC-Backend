package mcnc.survwey.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void deleteSurveyFromRedis(Long surveyId) {
        String key = "survey:end:" + surveyId;
        redisTemplate.delete(key);
    }

    public void saveSurveyExpireTime(Long surveyId, LocalDateTime endDateTime) {
        // 현재 시간과 종료 시간의 차이를 구하기
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(currentTime, endDateTime);

        // 종료 시간이 이미 지난 경우 예외 처리 (TTL이 음수가 될 수 있으므로)
        if (duration.isNegative()) {
            throw new IllegalArgumentException("The end time must be in the future.");
        }

        // TTL을 초 단위로 변환
        long ttlSeconds = duration.getSeconds();

        // Redis에 TTL 설정 (survey:end:{surveyId} 키에)
        String key = "survey:end:" + surveyId;
        redisTemplate.opsForValue().set(key, "active", ttlSeconds, TimeUnit.SECONDS);
    }
}
