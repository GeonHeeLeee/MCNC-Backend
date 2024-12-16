package mcnc.survwey.api.survey.response.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.response.dto.answered.AnsweredSurveyDTO;
import mcnc.survwey.api.survey.response.dto.reply.SurveyReplyDTO;
import mcnc.survwey.api.survey.response.dto.result.SurveyResultDTO;
import mcnc.survwey.api.survey.response.service.AnsweredSurveyService;
import mcnc.survwey.api.survey.response.service.SurveyReplyService;
import mcnc.survwey.api.survey.response.service.SurveyResultService;
import mcnc.survwey.domain.respond.service.RespondService;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.service.SurveyService;
import mcnc.survwey.global.config.SessionContext;
import mcnc.survwey.global.exception.custom.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static mcnc.survwey.global.exception.custom.ErrorCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey/response")
@Tag(name = "설문 응답 관리", description = "본인이 생성/응답한 설문 결과, 설문 응답 API")
public class SurveyResponseController {

    private final SurveyReplyService surveyReplyService;
    private final SurveyResultService surveyResultService;
    private final AnsweredSurveyService answeredSurveyService;
    private final RespondService respondService;
    private final SurveyService surveyService;

    /**
     * 응답 저장
     * @param surveyReplyDTO
     * @return
     */
    @PostMapping()
    @Operation(summary = "설문 응답", description = "SUBJECTIVE인 경우, SelectionId는 주지 않고, response에 응답을 담아서 주면 됨<br>" +
            "객관식(OBJ_MULTI, OBJ_SINGLE)인 경우 SelectionId를 포함해서 주면 됨<br>기타인 경우 기타의 응답을 response에 담아서 주면 됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 응답 성공"),
            @ApiResponse(responseCode = "400", description = """
                잘못된 요청:
                - 해당 아이디의 사용자가 존재하지 않습니다.
                - 해당 아이디의 설문이 존재하지 않습니다.
                - 해당 설문은 종료된 설문입니다.
                - 이미 해당 설문에 응답하셨습니다.
                - 해당 요청의 질문은 해당 설문의 질문이 아니거나 응답하지 않은 질문이 있습니다.(다른 설문의 QuesId를 요청하거나 설문의 모든 질문에 답하지 않았을때)
                """),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Object> replyToSurvey(@Valid @RequestBody SurveyReplyDTO surveyReplyDTO) {
        String userId = SessionContext.getCurrentUser();
        surveyReplyService.saveSurveyReply(surveyReplyDTO, userId);
        return ResponseEntity.ok(null);
    }

    /**
     * 설문 결과(통계) 조회
     * @param surveyId
     * @return
     */
    @GetMapping("/result/{surveyId}")
    @Operation(summary = "본인이 생성한 설문 결과 조회", description = "PathVariable로 설문 아이디를 넣어 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "errorMessage : 해당 아이디의 설문이 존재하지 않습니다."),
            @ApiResponse(responseCode = "403", description = "errorMessage : 본인이 생성한 설문이 아닙니다.")
    })
    public ResponseEntity<SurveyResultDTO> getSurveyResults(@PathVariable Long surveyId) {
        String userId = SessionContext.getCurrentUser();
        SurveyResultDTO surveyResultDTO = surveyResultService.getSurveyResponsesResult(surveyId, userId);
        return ResponseEntity.ok(surveyResultDTO);
    }


    @GetMapping("/{surveyId}")
    @Operation(summary = "본인이 응답한 특정 설문 조회", description = "PathVariable로 설문 아이디를 넣어 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "errorMessage : 해당 설문에 참여하지 않았습니다."),
            @ApiResponse(responseCode = "400", description = "errorMessage : 해당 아이디의 설문이 존재하지 않습니다.")
    })
    public ResponseEntity<Object> getUserAnsweredSurvey(@PathVariable("surveyId") Long surveyId) {
        String userId = SessionContext.getCurrentUser();
        //응답하지 않은 설문이면 에러 전송
        if (!respondService.hasUserRespondedToSurvey(surveyId, userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, HAS_NOT_RESPOND_TO_SURVEY);
        }
        AnsweredSurveyDTO userRespondedSurvey = answeredSurveyService.getUserAnsweredSurvey(surveyId, userId);
        return ResponseEntity.ok(userRespondedSurvey);
    }

    @GetMapping("/verify/{surveyId}")
    @Operation(summary = "해당 설문에 본인이 응답 했는지 확인", description = "PathVariable로 surveyId 받아서 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "응답하지 않은 설문"),
            @ApiResponse(responseCode = "400", description = """
                잘못된 요청:
                - 해당 아이디의 사용자가 존재하지 않습니다.
                - 해당 아이디의 설문이 존재하지 않습니다.
                """
            ),
            @ApiResponse(responseCode = "401", description = "세션이 존재하지 않습니다."),
            @ApiResponse(responseCode = "409", description = "해당 설문에 이미 응답하셨습니다.")
    })
    public ResponseEntity<Object> getUserRespondedSurvey(@PathVariable("surveyId") Long surveyId){
        String userId = SessionContext.getCurrentUser();
        Survey survey = surveyService.findBySurveyId(surveyId);
        if(respondService.hasUserRespondedToSurvey(surveyId, userId)){
            throw new CustomException(HttpStatus.CONFLICT, HAS_ALREADY_RESPOND_TO_SURVEY);
        }
        if (survey.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new CustomException(HttpStatus.GONE, EXPIRED_SURVEY);
        }else {
            return ResponseEntity.ok("참여하지 않은 설문입니다.");
        }
    }

}
