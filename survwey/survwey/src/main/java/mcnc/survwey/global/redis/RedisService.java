package mcnc.survwey.global.redis;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final long CODE_EXPIRATION_MINUTES = 10; // 유효시간 10분

    // TODO: 현재 에외 처리가 완벽하지 않은데 추후 예외처리 더 확실히 하기

    public void deleteSurveyFromRedis(String creatorId, Long surveyId) {
        String key = generateRedisKey(creatorId, surveyId);;
        Boolean wasDeleted = redisTemplate.delete(key);
        if (Boolean.FALSE.equals(wasDeleted)) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNEXPECTED_REDIS_ERROR);
        }
    }

    public void saveSurveyExpireTime(Long surveyId, String creatorId, LocalDateTime endDateTime) {
        // 현재 시간과 종료 시간의 차이를 구하기
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(currentTime, endDateTime);

        // 종료 시간이 이미 지난 경우
        if (duration.isNegative()) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNEXPECTED_REDIS_ERROR);
        }

        // TTL을 초 단위로 변환
        long ttlSeconds = duration.getSeconds();

        // Redis에 TTL 설정 (survey:end:{surveyId} 키에)
        String key = generateRedisKey(creatorId, surveyId);
        redisTemplate.opsForValue().set(key, "active", ttlSeconds, TimeUnit.SECONDS);
    }

    public void expireImmediately(String userId, Long surveyId) {
        String key = generateRedisKey(userId, surveyId);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.expire(key, 1, TimeUnit.SECONDS);
        } else {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNEXPECTED_REDIS_ERROR);
        }
    }

    private String generateRedisKey(String creatorId, Long surveyId){
        return "survey:end:" + creatorId + "/" + surveyId;
    }
}
