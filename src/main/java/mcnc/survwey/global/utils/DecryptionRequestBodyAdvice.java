package mcnc.survwey.global.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class DecryptionRequestBodyAdvice implements RequestBodyAdvice {

    private final EncryptionUtil encryptionUtil;

    /**
     * DTO에서 @DecryptField가 붙은 어노테이션에 대하여 복호화 수행
     * - ControllerAdvice의 RequstBodyAdvice를 구현하여 @Valid보다 먼저 수행
     *  - 즉, ExceptionHandler보다 먼저 수행
     * @param object
     * @throws IllegalAccessException
     * @Author 이건희
     */
    private void decryptObject(Object object) throws IllegalAccessException {
        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            //DecryptField 어노테이션이 붙고, type이 String인 것에 대해서 복호화 수행
            if (field.isAnnotationPresent(DecryptField.class) && field.getType() == String.class) {
                field.setAccessible(true);
                String encryptedValue = (String) field.get(object);
                log.info(encryptedValue);
                if (encryptedValue != null && !encryptedValue.isEmpty()) {
                    try {
                        String decryptedValue = encryptionUtil.decryptText(encryptedValue);
                        field.set(object, decryptedValue);
                        log.info(decryptedValue);
                    } catch (Exception e) {
                        log.error("복호화 중 오류 발생: {}", e.getMessage());
                        throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ENCRYPTED_FIELD);
                    }
                }
            }
        }
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        String packageName = methodParameter.getContainingClass().getPackageName();
        //해당하는 패키지 경로인 요청/응답에 대한 처리
        return packageName.startsWith("mcnc.survwey.api.account") ||
                packageName.startsWith("mcnc.survwey.api.auth");
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        // 바디 읽기 전 특별한 처리가 필요 없으면 그대로 반환
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        try {
            if (body != null) {
                decryptObject(body); // 복호화 로직 실행
            }
        } catch (Exception e) {
            log.error("RequestBody 복호화 중 오류 발생: {}", e.getMessage());
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ENCRYPTED_FIELD);
        }
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                  Class<? extends HttpMessageConverter<?>> converterType) {
        return body; // 비어 있는 바디 처리 (필요 없으면 그대로 반환)
    }


}
