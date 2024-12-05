package mcnc.survwey.global.utils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(1) // 우선순위 설정
public class DecryptionAspect {

    private final EncryptionUtil encryptionUtil;

    /**
     * 실행 시점에 AOP 적용
     * - 메서드의 인자(joinPoint.getArgs())를 가져와서 복호화
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* mcnc.survwey.api.account.controller.*.*(..)) || " +
            "execution(* mcnc.survwey.api.auth.controller.*.*(..))")
    public Object decryptFields(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg != null) {
                decryptObject(arg);
            }
        }

        return joinPoint.proceed(args);
    }

    private void decryptObject(Object object) throws IllegalAccessException {
        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(DecryptField.class) && field.getType() == String.class) {
                field.setAccessible(true);
                String encryptedValue = (String) field.get(object);

                if (encryptedValue != null && !encryptedValue.isEmpty()) {
                    try {
                        String decryptedValue = encryptionUtil.decrypt(encryptedValue);
                        field.set(object, decryptedValue);
                    } catch (Exception e) {
                        log.error("AOP 복호화 중 오류 발생: {}", e.getMessage());
                        throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ENCRYPTED_FIELD);
                    }
                }
            }
        }
    }
}
