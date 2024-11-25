package mcnc.survwey.domain.mail.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.mail.service.MailService;
import mcnc.survwey.domain.mail.utils.EncryptionUtil;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;
    private final EncryptionUtil encryptionUtil;
    private final Map<String, String> keyStorage = new HashMap<>();

    @PostMapping("/send/{surveyId}")
    public ResponseEntity<String> sendMail(@PathVariable Long surveyId) throws Exception{
        String userId = SessionContext.getCurrentUser();
        try{
            String key = encryptionUtil.generatedRandomKey();
            String token = encryptionUtil.encrypt(surveyId.toString(), key);
            keyStorage.put(token, key);

            String surveyLink = mailService.encryptedLink(surveyId, key);
            //링크 암호화
            mailService.sendLinkMessage(userId, surveyId, surveyLink);
            return ResponseEntity.ok("메일 발송!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("해당 링크는 잘못된 링크입니다.");
        }
    }

    /**
     * 링크 복호화 후 해당 설문으로 이동
     * @param token
     * @return
     * @throws IOException
     */
    @GetMapping("/{token}")
    public ResponseEntity<String> handleRedirect(@PathVariable String token) throws Exception {
        log.info("handleRedirect 호출됨: surveyId={}", token);
        try {
            String key = keyStorage.get(token);
            String decryptedSurveyId = encryptionUtil.decrypt(token, key);

            String decryptedUrl = mailService.decryptedLink(decryptedSurveyId);

            return ResponseEntity.status(HttpStatus.FOUND).header("Location", decryptedUrl).build();//302Found
        } catch (Exception e) {
            log.error("Error during redirect handling: ", e);
            return ResponseEntity.badRequest().body("해당 링크는 잘못된 링크입니다.");
        }
    }

}
