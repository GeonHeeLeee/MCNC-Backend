package mcnc.survwey.domain.mail.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.mail.service.MailService;
import mcnc.survwey.domain.mail.utils.EncryptionUtil;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    - 사용자가 만들지 않은 설문 : "본인이 생성한 설문이 아닙니다."
                    """),
            @ApiResponse(responseCode = "401", description = "세션이 유효하지 않음")
    })
    public ResponseEntity<String> sendMail(@PathVariable Long surveyId) throws Exception{
         String userId = SessionContext.getCurrentUser();
         mailService.sendLinkMessage(userId, surveyId);
         return ResponseEntity.ok("메일 발송!");
    }

    /**
     * 링크 복호화 후 해당 설문으로 이동
     *
     * @param token
     * @return
     */
    @GetMapping("/{token}")
    @Operation(summary = "해당 설문으로 이동", description = "@PathVariable 로 해당하는 토큰을 받음 <br> 세션이 없을 시 decryptedUrl 값을 응답")
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
    public ResponseEntity<Map<String, String>> handleRedirect(@PathVariable String token) {
        String userId = SessionContext.getCurrentUser();
        String surveyId = encryptionUtil.decrypt(token);
        String decryptedUrl = mailService.decryptLink(surveyId);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("decryptedUrl", decryptedUrl));
        } else {
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", decryptedUrl).build();//302Found}
        }
    }

//    /**
//     * 사용자 비밀번호 찾기
//     * 사용자가 비밀번호를 찾기를 위한 인증
//     * @param userId
//     * @return
//     */
//    @PostMapping("/password")
//    public ResponseEntity<String> modifyPasswordSendMail(@RequestBody String userId){
//        mailService.sendPasswordModifyLink(userId);
//
//        return ResponseEntity.ok("임시 번호 발송");
//    }

}
