package mcnc.survwey.domain.mail.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String AES = "AES";
    private static final byte[] SECRET_KEY = "1234567890123456".getBytes();
    //AES 암호화 방식, 16바이트 키

    /**
     * URL 암호화
     * @param url
     * @return
     * @throws Exception
     */
    public static String encrypt(String url) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY, AES);
        //암호화 스펙
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);//암호화 모드 초기화
        byte[] encrypted = cipher.doFinal(url.getBytes());//데이터 암호화
        return Base64.getUrlEncoder().encodeToString(encrypted);//URL BASE64로 변환
    }

    /**
     * 암호화된 URL 복호화
     * @param encryptedUrl
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptedUrl) throws Exception{
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY, AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);//복호화 모드 초기화
        byte[] decrypted = Base64.getUrlDecoder().decode(encryptedUrl);//URL BASE64 디코딩
        return new String(cipher.doFinal(decrypted));//복호화 후 문자열로 변환
    }
}
