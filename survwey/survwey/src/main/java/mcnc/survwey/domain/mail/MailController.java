package mcnc.survwey.domain.mail;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/send/{surveyId}")
    public ResponseEntity<String> sendMail(@RequestBody String link, @PathVariable Long surveyId){
        String userId = SessionContext.getCurrentUser();
        mailService.sendLinkMessage(userId, surveyId, link);

        return ResponseEntity.ok("메일 발송 완료");

    }

}
