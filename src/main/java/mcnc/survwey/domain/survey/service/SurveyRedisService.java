package mcnc.survwey.domain.survey.service;

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
public class SurveyRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String SURVEY_EXPIRATION_KEY_PREFIX = "survey:end:";

    /**
     * 설문 Redis에서 삭제
     * - 설문 조기 종료나 수정 등의 이유로 설문 삭제 시 실행
     * - Redis에서 해당 키가 없어도 예외가 발생하지 않음
     * @param creatorId
     * @param surveyId
     */
    public void deleteSurveyFromRedis(String creatorId, Long surveyId) {
        String key = generateRedisKey(creatorId, surveyId);
        redisTemplate.delete(key);
    }

    /**
     * 설문 생성 시 만료 시간 설정
     * - 만료일이 현재시간 이전인 경우 예외 발생
     * - 그렇지 않을 경우 만료일과 현재 시간의 차이 동안 유효하도록 Redis에 저장
     * @param surveyId
     * @param creatorId
     * @param expireDateTime
     */
    public void saveSurveyExpireTime(Long surveyId, String creatorId, LocalDateTime expireDateTime) {
        // 현재 시간과 종료 시간의 차이를 구하기
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(currentTime, expireDateTime);

        // 종료 시간이 이미 지난 경우 예외 발생
        if (duration.isNegative()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_EXPIRE_DATE);
        }

        // TTL을 초 단위로 변환
        long ttlSeconds = duration.getSeconds();

        String key = generateRedisKey(creatorId, surveyId);
        redisTemplate.opsForValue().set(key, "active", ttlSeconds, TimeUnit.SECONDS);
    }


    /**
     * 설문 즉시 종료
     * - Redis에서 해당 키가 없어도 예외가 발생하지 않음
     * - 해당 키 만료하여 바로 이벤트 발생시킴
     * @param userId
     * @param surveyId
     */
    public void expireImmediately(String userId, Long surveyId) {
        String key = generateRedisKey(userId, surveyId);
        redisTemplate.expire(key, 1, TimeUnit.SECONDS);
    }


    /**
     * Redis 키의 만료 시간 재설정
     * @param creatorId
     * @param surveyId
     * @param expireDateTime
     * @return
     */
    public void resetExpireTime(String creatorId, Long surveyId, LocalDateTime expireDateTime) {
        String key = generateRedisKey(creatorId, surveyId);
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(currentTime, expireDateTime);
        long ttlSeconds = duration.getSeconds();
        //만료시간 재설정
        redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }


    /**
     * Redis 저장할 키 생성
     * @param creatorId
     * @param surveyId
     * @return
     */
    private String generateRedisKey(String creatorId, Long surveyId) {
        return SURVEY_EXPIRATION_KEY_PREFIX + creatorId + "/" + surveyId;
    }

    /**
     * Redis에서 설문 키가 존재하는지 확인
     * @param creatorId
     * @param surveyId
     * @return
     */
    public boolean isSurveyExists(String creatorId, Long surveyId) {
        String key = generateRedisKey(creatorId, surveyId);
        return redisTemplate.hasKey(key);  // 해당 키가 Redis에 존재하면 true, 아니면 false
    }
}
