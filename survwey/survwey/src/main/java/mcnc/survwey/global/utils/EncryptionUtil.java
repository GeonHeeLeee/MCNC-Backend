package mcnc.survwey.global.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.*;


@Slf4j
@Component
public class EncryptionUtil {

    @Value("${ENCRYPTION_SECRET_KEY}")
    private String secretKey;


    /**
     * 암호화 모드 설정
     * @param mode
     * @return
     * @throws Exception
     */
    private Cipher initializeCipher(int mode) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(UTF_8), "AES");//키 세팅 -> AES 기준으로 secretKey 부분에 들어오는 길이에 따라 암호화 방식 결정
        Cipher cipher = getInstance("AES/CBC/PKCS5Padding");//AES 알고리즘으로 CBC 모드, PKCS5Padding scheme 으로 초기화
        IvParameterSpec ivParameterSpec = new IvParameterSpec(secretKey.substring(0, 16).getBytes());//키를 16 바이트로 잘라 초기화 벡터 byte 로 변경
        cipher.init(mode, keySpec, ivParameterSpec);//암호 모드 결정
        return cipher;
    }

    /**
     * URL 파라미터 암호화
     *
     * @param surveyId
     * @return
     */
    public String encryptURL(String url, Long surveyId) {
        //EncryptLink에 중복 검사, Survey, User 중복 조회들 있어서 따로 뺐음
        String encryptedSurveyId = encryptText(surveyId.toString());
        return url + encryptedSurveyId;
    }



    /**
     * AES/CBC 비밀키로 암호화
     * 16바이트 키 값은 환경변수로 저장됨
     * @param rawText
     * @return
     */
    public String encryptText(String rawText) {
        try {
            Cipher cipher = initializeCipher(ENCRYPT_MODE);
            return Base64.getUrlEncoder().encodeToString(cipher.doFinal(rawText.getBytes(UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패: " + e.getMessage());
        }
    }


    /**
     * AES/CBC 비밀키로 복호화
     * @param encryptedText
     * @return
     */
    public String decryptText(String encryptedText) {
        try {
            Cipher cipher = initializeCipher(DECRYPT_MODE);
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedText); // Base64 디코딩
            byte[] decryptedBytes = cipher.doFinal(decodedBytes); // 복호화 수행
            return new String(decryptedBytes, UTF_8); // UTF-8로 변환하여 반환
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패: " + e.getMessage());
        }
    }

    /**
     * AES/CBC 비밀키로 리스트 복호화
     * @param encryptedList
     * @return
     */
    public List<String> decryptList(List<String> encryptedList) {
        if (encryptedList == null) {
            return Collections.emptyList();
        }
        return encryptedList.stream()
                .map(this::decryptText)
                .collect(Collectors.toList());
    }
}
