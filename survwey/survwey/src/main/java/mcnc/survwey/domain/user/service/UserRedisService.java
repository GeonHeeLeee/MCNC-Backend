package mcnc.survwey.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final long CODE_EXPIRATION_MINUTES = 10; // 인증번호 유효시간 (10분)
    private static final long VERIFIED_STATUS_EXPIRATION_MINUTES = 5; //인증 완료 유효시간(5분)
    private static final String VERIFICATION_KEY_PREFIX = "auth:code:";
    private static final String VERIFIED_KEY_PREFIX = "auth:verified:";

    /**
     * 인증 번호 Redis에 저장
     * - 중복되는 키가 있을 경우 덮어쓰기
     * @param userId
     * @param code
     */
    public void saveVerificationCode(String userId, String code) {
        String key = VERIFICATION_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 인증번호 검증
     * - 키가 존재하면 삭제 후 true 반환
     * - 키가 존재하지 않거나 유효하지 않을 시 false 반환
     * @param userId
     * @param inputCode
     * @return
     */
    public boolean verifyCode(String userId, String inputCode) {
        String key = VERIFICATION_KEY_PREFIX + userId;
        String savedCode = redisTemplate.opsForValue().get(key);
        if (savedCode != null && savedCode.equals(inputCode)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /**
     * 인증 상태 저장
     * - 인증번호 인증이 완료된 사용자가 비밀번호 변경 시에 필요한 상태 저장
     * @param userId
     */
    public void saveVerifiedStatus(String userId) {
        String verifiedKey = VERIFIED_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(verifiedKey, "true", VERIFIED_STATUS_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 인증을 확인
     * - 비밀번호 변경 시 이미 인증이 완료된 사용자인지 검증
     * @param userId
     * @return
     */
    public boolean isVerified(String userId) {
        String verifiedKey = VERIFIED_KEY_PREFIX + userId;
        String status = redisTemplate.opsForValue().get(verifiedKey);
        return "true".equals(status);
    }

    /**
     * 작업 수행 후 인증 상태 삭제
     * - redis의 delete는 키가 없는 상태에서 삭제해도 문제가 되지 않음
     * @param userId
     */
    public void deleteVerifiedStatus(String userId) {
        String verifiedKey = VERIFIED_KEY_PREFIX + userId;
        redisTemplate.delete(verifiedKey);
    }
}
