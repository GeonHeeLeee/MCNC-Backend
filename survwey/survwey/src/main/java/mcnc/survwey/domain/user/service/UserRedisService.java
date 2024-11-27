package mcnc.survwey.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserRedisService {

    private final StringRedisTemplate redisTemplate;

    private static final long CODE_EXPIRATION_MINUTES = 10; // 인증번호 유효시간 (10분)
    private static final long VERIFIED_STATUS_EXPIRATION_MINUTES = 5; //인증 완료 유효시간(5분)
    private static final String VERIFICATION_KEY_PREFIX = "auth:code:";
    private static final String VERIFIED_KEY_PREFIX = "auth:verified:";

    // 인증번호 저장
    public void saveVerificationCode(String userId, String code) {
        String key = VERIFICATION_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    // 인증번호 검증
    public boolean verifyCode(String userId, String inputCode) {
        String key = VERIFICATION_KEY_PREFIX + userId;
        String savedCode = redisTemplate.opsForValue().get(key);
        if (savedCode != null && savedCode.equals(inputCode)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    // 인증 상태 저장 (인증 완료 후)
    public void saveVerifiedStatus(String userId) {
        String verifiedKey = VERIFIED_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(verifiedKey, "true", VERIFIED_STATUS_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    // 인증 상태 확인
    public boolean isVerified(String userId) {
        String verifiedKey = VERIFIED_KEY_PREFIX + userId;
        String status = redisTemplate.opsForValue().get(verifiedKey);
        return "true".equals(status);
    }

    // 인증 상태 삭제
    public void deleteVerifiedStatus(String userId) {
        String verifiedKey = VERIFIED_KEY_PREFIX + userId;
        redisTemplate.delete(verifiedKey);
    }
}
