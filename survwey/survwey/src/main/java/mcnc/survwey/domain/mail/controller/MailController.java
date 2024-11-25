package mcnc.survwey.domain.mail.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.mail.service.MailService;
import mcnc.survwey.domain.mail.utils.EncryptionUtil;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.global.config.SessionContext;
import mcnc.survwey.global.exception.custom.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;
    private final EncryptionUtil encryptionUtil;
    private final SurveyService surveyService;
    private final Map<String, String> keyStorage = new HashMap<>();

    /**
     * 설문 링크 암호화 후 이메일 전송
     * @param surveyId
     * @return
     * @throws Exception
     */
    @PostMapping("/send/{surveyId}")
    @Operation(summary = "이메일 전송", description = "요청 Body 없이 요청<br>@PathVariable 로 해당하는 SurveyId만 받음")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메일 발송 성공"),
            @ApiResponse(responseCode = "400", description = """
                    잘못된 요청:
                    - 잘못된 링크 : "해당 링크는 잘못된 링크입니다."
                    - 만료일 지났을 경우 : "해당 설문은 종료된 설문입니다."
                    - 존재하지 않은 설문 : "해당 아이디의 설문이 존재하지 않습니다."
                    """)
    })
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
    @Operation(summary = "해당 설문으로 이동", description = "@PathVariable 로 해당하는 토큰을 받음")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "해당 설문으로 이동"),
            @ApiResponse(responseCode = "400", description = """
                    잘못된 요청:
                    - 잘못된 링크 : "해당 링크는 잘못된 링크입니다."
                    - 만료일 지났을 경우 : "해당 설문은 종료된 설문입니다."
                    - 존재하지 않은 설문 : "해당 아이디의 설문이 존재하지 않습니다."
                    """),
            @ApiResponse(responseCode = "401", description = "세션이 유효하지 않음")
    })
    public ResponseEntity<String> handleRedirect(@PathVariable String token) throws Exception {
        try {
            String key = keyStorage.get(token);
            String decryptedSurveyId = encryptionUtil.decrypt(token, key);

            Survey survey = surveyService.findBySurveyId(Long.parseLong(decryptedSurveyId));
            if (survey.getExpireDate().isBefore(LocalDateTime.now())
                    || survey.getExpireDate().isEqual(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("해당 설문은 종료된 설문입니다.");
            }

            String decryptedUrl = mailService.decryptedLink(decryptedSurveyId);

            return ResponseEntity.status(HttpStatus.FOUND).header("Location", decryptedUrl).build();//302Found
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("해당 링크는 잘못된 링크입니다.");
        }
    }

}
