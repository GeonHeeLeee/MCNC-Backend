package mcnc.survwey.domain.mail.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.mail.service.MailService;
import mcnc.survwey.domain.mail.utils.EncryptionUtil;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;
    private final EncryptionUtil encryptionUtil;

    /**
     * 설문 링크 암호화 후 이메일 전송
     *
     * @param surveyId
     * @return
     * @throws Exception
     */
    @PostMapping("/send/{surveyId}")
    @Operation(summary = "이메일 전송", description = "요청 Body \"email\" : [\"test@test.com\", \"asd@asd.com\"] 초대할 사람 이메일 <br>@PathVariable 로 해당하는 SurveyId 받음")
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
    public ResponseEntity<Object> sendInvitationMail(@PathVariable("surveyId") Long surveyId, @RequestBody Map<String, List<String>> requestBody) throws Exception {
        try {
            if(!requestBody.containsKey("email")) {
                return ResponseEntity.badRequest().body(null);
            }
            String userId = SessionContext.getCurrentUser();
            mailService.sendInvitationLink(userId, surveyId, requestBody.get("email"));
            return ResponseEntity.ok("메일 발송!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("errorMessage", "메일 전송 실패"));
        }

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
    public ResponseEntity<Map<String, String>> mailHandleRedirection(HttpServletRequest request, @PathVariable("token") String token) {
        HttpSession session = request.getSession(false);
        String decryptedSurveyId = encryptionUtil.decrypt(token);
        String decryptedUrl = mailService.decryptLink(decryptedSurveyId);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("decryptedUrl", decryptedUrl));
        } else {
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", decryptedUrl).build();//302Found}
        }
    }

}
