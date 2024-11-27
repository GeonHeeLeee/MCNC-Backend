package mcnc.survwey.domain.mail.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Slf4j
@Component
public class EncryptionUtil {

    @Value("${ENCRYPTION_SECRET_KEY}")
    private String secretKey;

    /**
     * AES/CBC 비밀키로 URL 파라미터 암호화
     * 16바이트 키 값은 환경변수로 저장됨
     * @param surveyId
     * @return
     */
    public String encrypt(String surveyId) {
        try{
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            // 암호화 스펙
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(secretKey.substring(0, 16).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec); // 암호화 모드 초기화
            byte[] encrypted = cipher.doFinal(surveyId.getBytes()); // 데이터 암호화
            return Base64.getUrlEncoder().encodeToString(encrypted); // URL BASE64로 변환
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 링크는 잘못된 링크입니다.", e);
        }

    }

    /**
     * AES/CBC 방식으로 암호화된 URL 파라미터 복호화
     * 환경변수에 저장된 키값으로 복호화
     * @param encryptedUrl
     * @return
     */
    public String decrypt(String encryptedUrl) {
        try{
            // URL-safe Base64 문자열에서 `-`와 `_`를 각각 `+`와 `/`로 변환
            String base64DecodedUrl = encryptedUrl.replace("-", "+").replace("_", "/");

            byte[] decodedBytes = Base64.getDecoder().decode(base64DecodedUrl);// Base64 디코딩

            // 복호화 진행
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(secretKey.substring(0, 16).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec); // 복호화 모드 초기화

            byte[] decrypted = cipher.doFinal(decodedBytes); // 데이터 복호화
            String result = new String(decrypted);

            return result; // 복호화된 URL 반환
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 링크는 잘못된 링크입니다.", e);
        }

    }
}
