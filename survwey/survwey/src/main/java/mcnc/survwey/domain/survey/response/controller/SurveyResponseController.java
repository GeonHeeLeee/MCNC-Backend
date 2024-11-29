package mcnc.survwey.domain.survey.response.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.respond.service.RespondService;
import mcnc.survwey.domain.survey.response.dto.SurveyResultDTO;
import mcnc.survwey.domain.survey.response.dto.SurveyReplyDTO;
import mcnc.survwey.domain.survey.response.dto.SurveyResponseDTO;
import mcnc.survwey.domain.survey.response.service.SurveyResponseService;
import mcnc.survwey.global.config.SessionContext;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey/response")
@Tag(name = "설문 응답 관리", description = "본인이 생성/응답한 설문 결과, 설문 응답 API")
public class SurveyResponseController {

    private final SurveyResponseService surveyResponseService;
    private final RespondService respondService;
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
    public ResponseEntity<Object> responseToSurvey(@Valid @RequestBody SurveyReplyDTO surveyReplyDTO) {
        String userId = SessionContext.getCurrentUser();
        surveyResponseService.saveSurveyReply(surveyReplyDTO, userId);
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
        SurveyResultDTO surveyResponse = surveyResponseService.getSurveyResponsesResult(surveyId, userId);
        return ResponseEntity.ok(surveyResponse);
    }


    @GetMapping("/{surveyId}")
    @Operation(summary = "본인이 응답한 특정 설문 조회", description = "PathVariable로 설문 아이디를 넣어 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "errorMessage : 해당 설문에 참여하지 않았습니다."),
            @ApiResponse(responseCode = "400", description = "errorMessage : 해당 아이디의 설문이 존재하지 않습니다.")
    })
    public ResponseEntity<Object> getUserSurveyResponse(@PathVariable("surveyId") Long surveyId) {
        String userId = SessionContext.getCurrentUser();
        //응답하지 않은 설문이면 에러 전송
        if (!respondService.hasUserRespondedToSurvey(surveyId, userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.HAS_NOT_RESPOND_TO_SURVEY);
        }
        SurveyResponseDTO userRespondedSurvey = surveyResponseService.getUserRespondedSurvey(surveyId, userId);
        return ResponseEntity.ok(userRespondedSurvey);
    }

}
