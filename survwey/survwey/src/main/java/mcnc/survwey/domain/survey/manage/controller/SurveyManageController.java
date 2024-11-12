package mcnc.survwey.domain.survey.manage.controller;


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
    public ResponseEntity<Object> createSurvey(@Valid @RequestBody SurveyWithDetailDTO surveyWithDetailDTO) {
        try {
            String userId = SessionContext.getCurrentUser();
            Survey survey = surveyManageService.saveSurveyWithDetails(surveyWithDetailDTO, userId);
            return ResponseEntity.ok().body(Collections.singletonMap("surveyId", survey.getSurveyId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{surveyId}")
    public ResponseEntity<Object> deleteSurvey(@PathVariable("surveyId") Long surveyId) {
        if (surveyManageService.deleteSurvey(surveyId)) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.badRequest().body(Collections.singletonMap("errorMessage", "해당 아이디의 설문이 존재하지 않습니다."));
    }

    @PostMapping("/response")
    public ResponseEntity<Object> responseSurvey(@RequestBody SurveyResponseDTO surveyResponseDTO) {
        try {
            String userId = SessionContext.getCurrentUser();
            surveyManageService.saveSurveyResponses(surveyResponseDTO, userId);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("errorMessage", "응답 저장에 실패했습니다."));
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<Object> surveyModify(@Valid @RequestBody SurveyWithDetailDTO surveyWithDetailDTO) {
        String userId = SessionContext.getCurrentUser();

        SurveyWithDetailDTO updatedSurvey = surveyManageService.surveyModifyWithDetails(surveyWithDetailDTO, userId);
        log.info(updatedSurvey.toString());
        return ResponseEntity.ok().body(updatedSurvey);
    }
}
