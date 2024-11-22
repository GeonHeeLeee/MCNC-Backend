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
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/send/{surveyId}")
    public ResponseEntity<String> sendMail(@RequestBody String link, @PathVariable Long surveyId) throws Exception{
        String userId = SessionContext.getCurrentUser();
        try{
            String surveyLink = mailService.linkEncryption(link, surveyId);
            //링크 암호화
            mailService.sendLinkMessage(userId, surveyId, surveyLink);
            return ResponseEntity.ok("메일 발송!");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("해당 링크는 잘못된 링크입니다.");
        }
    }

    @GetMapping("/redirect")
    public ResponseEntity<String> handleRedirect(@RequestParam String token) throws IOException {
        try {
            String decryptedUrl = EncryptionUtil.decrypt(token);
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", decryptedUrl).build();//302Found
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("해당 링크는 잘못된 링크입니다.");
        }

    }

}
