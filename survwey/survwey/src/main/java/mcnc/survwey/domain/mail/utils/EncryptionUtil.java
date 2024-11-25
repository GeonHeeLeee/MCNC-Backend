package mcnc.survwey.domain.mail.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class EncryptionUtil {

    public String generatedRandomKey() throws Exception{
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomKey = new byte[16];//AES-128 -> 16바이트
        secureRandom.nextBytes(randomKey);
        return Base64.getEncoder().encodeToString(randomKey);//키 Base64로 인코딩 -> 문자열로 반환
    }

    public String encrypt(String url, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        // 암호화 스펙
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(key.substring(0, 16).getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec); // 암호화 모드 초기화
        byte[] encrypted = cipher.doFinal(url.getBytes()); // 데이터 암호화
        return Base64.getUrlEncoder().encodeToString(encrypted); // URL BASE64로 변환
    }
    public String decrypt(String encryptedUrl, String key) throws Exception {
        // URL-safe Base64 문자열에서 `-`와 `_`를 각각 `+`와 `/`로 변환
        String base64DecodedUrl = encryptedUrl.replace("-", "+").replace("_", "/");

        byte[] decodedBytes = Base64.getDecoder().decode(base64DecodedUrl);// Base64 디코딩

        // 복호화 진행
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(key.substring(0, 16).getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec); // 복호화 모드 초기화

        byte[] decrypted = cipher.doFinal(decodedBytes); // 데이터 복호화
        String result = new String(decrypted);

        return result; // 복호화된 URL 반환
    }
}
