package mcnc.survwey.api.mail.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.mail.service.MailService;
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
@Tag(name = "이메일 전송", description = "이메일 초대 링크 발송")
public class MailController {

    private final MailService mailService;

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
            @ApiResponse(responseCode = "401", description = "세션이 유효하지 않음"),
            @ApiResponse(responseCode = "403", description = "본인이 생성한 설문이 아닙니다."),
            @ApiResponse(responseCode = "410", description = "해당 설문은 종료된 설문입니다.")
    })
    public ResponseEntity<Object> sendInvitationMail(@PathVariable("surveyId") Long surveyId, @RequestBody List<String> encryptedEmailList) {
        if (encryptedEmailList.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        String userId = SessionContext.getCurrentUser();
        mailService.sendInvitationLink(userId, surveyId, encryptedEmailList);
        return ResponseEntity.ok("메일 발송!");
    }

}
