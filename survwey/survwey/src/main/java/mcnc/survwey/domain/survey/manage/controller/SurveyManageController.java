package mcnc.survwey.domain.survey.manage.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.manage.dto.SurveyResponseDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.manage.service.SurveyManageService;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey/manage")
@Tag(name = "설문 관리", description = "설문 생성/삭제/응답/수정 API")
public class SurveyManageController {

    private final SurveyManageService surveyManageService;

    /**
     * 설문 생성
     * - 현재 SurveyWithDetailDTO에 생성에 필요 없는 surveyId, creatorId, quesId, selectionId들이 존재
     * - 재사용성을 위해 사용한 것이므로 요청 시에는 Id 불필요
     *
     * @param surveyWithDetailDTO
     * @return
     */
    @PostMapping("/create")
    @Operation(summary = "설문 생성", description = "현재 하단에 있는 surveyId, creatorId, quesId, selectionId는 제외하고 요청(백엔드에서 DTO를 재사용하느라 사용)<br>" +
            "또한 주관식(SUBJECTIVE인 경우 selectionList를 빈배열([])로 요청<br>OBJ_MULTI, OBJ_SINGLE 인 경우 selectionList안에 selectionId 제외하고 요청하면 됨(body만 포함)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "해당 아이디의 사용자가 존재하지 않습니다."),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Object> createSurvey(@Valid @RequestBody SurveyWithDetailDTO surveyWithDetailDTO) {
        try {
            String userId = SessionContext.getCurrentUser();
            Survey survey = surveyManageService.saveSurveyWithDetails(surveyWithDetailDTO, userId);
            return ResponseEntity.ok().body(Collections.singletonMap("surveyId", survey.getSurveyId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 설문 삭제
     * @param surveyId
     * @return
     */
    @DeleteMapping("/delete/{surveyId}")
    @Operation(summary = "설문 삭제", description = "PathVariable에 설문 ID를 넣어 요청하면 해당 설문에 해당하는 질문/보기/응답 모두 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "해당 아이디의 사용자가 존재하지 않습니다."),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Object> deleteSurvey(@PathVariable("surveyId") Long surveyId) {
        if (surveyManageService.deleteSurvey(surveyId)) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.badRequest().body(Collections.singletonMap("errorMessage", "해당 아이디의 설문이 존재하지 않습니다."));
    }

    /**
     * 응답 저장
     * @param surveyResponseDTO
     * @return
     */
    @PostMapping("/response")
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

    public ResponseEntity<Object> responseSurvey(@RequestBody SurveyResponseDTO surveyResponseDTO) {
        try {
            String userId = SessionContext.getCurrentUser();
            surveyManageService.saveSurveyResponses(surveyResponseDTO, userId);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("errorMessage", "응답 저장에 실패했습니다."));
        }
    }

    /**
     * 사용자가 자신이 만든 설문을 수정을 위해 삭제 후 생성
     * -해당 설문에 응답한 사람이 1명이라도 존재하면 설문 수정 불가
     * @param surveyWithDetailDTO
     * @return
     */

    @PostMapping("/modify")
    @Operation(summary = "설문 수정", description = "해당 설문에 1명이라도 응답한 사람이 존재하면 설문 수정 불가, 설문 수정 페이지에서 수정한 내용들을 바탕으로 기존 설문 삭제 후 새롭게 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 수정 성공"),
            @ApiResponse(responseCode = "400", description = """
                잘못된 요청:
                - 설문에 응답한 사람이 존재할 경우: 해당 설문에 답한 사용자가 이미 존재합니다.
                - 설문이 존재하지 않을 경우: 해당 설문이 존재하지 않습니다.
                - 삭제할 설문을 찾았지만 아이디가 다를 때: 해당 아이디의 설문이 존재하지 않습니다.
                """),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })

    public ResponseEntity<Object> surveyModify(@Valid @RequestBody SurveyWithDetailDTO surveyWithDetailDTO) {
        String userId = SessionContext.getCurrentUser();

        SurveyWithDetailDTO updatedSurvey = surveyManageService.surveyModifyWithDetails(surveyWithDetailDTO, userId);
        return ResponseEntity.ok().body(updatedSurvey);
    }

    /**
     * 설문 강제 종료
     * @param surveyId
     * @return
     */
    @GetMapping("/expired/{surveyId}")
    @Operation(summary = "설문 강제 종료", description = "해당 설문의 생성자가 강제 종료를 원할경우 만료일을 현재 시간으로 변경하여 강제 종료")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = """
               - 설문 강제 종료 성공     
               - 설문 강제 종료 취소"""),
            @ApiResponse(responseCode = "400", description = """
                잘못된 요청:
                - 본인이 생성한 설문이 아닌 경우: 본인이 만든 설문만 종료할 수 있습니다.
                - 설문이 존재하지 않을 경우: 해당 설문이 존재하지 않습니다.
                """),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Object> surveyExpiration(@PathVariable(value = "surveyId") Long surveyId) {
        String userId = SessionContext.getCurrentUser();
        surveyManageService.enforceCloseSurvey(userId, surveyId);
        return ResponseEntity.ok().body(surveyId);
    }
}
