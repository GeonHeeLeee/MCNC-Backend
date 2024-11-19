package mcnc.survwey.domain.survey.response.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.response.dto.SurveyResultDTO;
import mcnc.survwey.domain.survey.response.dto.SurveyResponseDTO;
import mcnc.survwey.domain.survey.response.service.SurveyResponseService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey/response")
@Tag(name = "설문 응답 관리", description = "본인이 생성/응답한 설문 결과, 설문 응답 API")
public class SurveyResponseController {

    private final SurveyResponseService surveyResponseService;
    /**
     * 응답 저장
     * @param surveyResponseDTO
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
                - try catch 쓰면 응답 저장에 실패했습니다.
                """),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Object> responseToSurvey(@RequestBody SurveyResponseDTO surveyResponseDTO) {
        try {
            String userId = SessionContext.getCurrentUser();
            surveyResponseService.saveSurveyResponses(surveyResponseDTO, userId);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("errorMessage", "응답 저장에 실패했습니다."));
        }
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
            @ApiResponse(responseCode = "401", description = "errorMessage : 본인이 생성한 설문이 아닙니다.")
    })
    public ResponseEntity<SurveyResultDTO> inquirySurveyResults(@PathVariable Long surveyId) {
        String userId = SessionContext.getCurrentUser();
        SurveyResultDTO surveyResponse = surveyResponseService.getSurveyResponse(surveyId, userId);
        return ResponseEntity.ok(surveyResponse);
    }


}
