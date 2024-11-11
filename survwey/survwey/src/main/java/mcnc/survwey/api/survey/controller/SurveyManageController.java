package mcnc.survwey.api.survey.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.CreateSurveyDTO;
import mcnc.survwey.api.survey.service.SurveyManageService;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey")
public class SurveyManageController {

    private final SurveyManageService surveyManageService;

    @PostMapping("/create")
    public ResponseEntity<Object> createSurvey(@Valid @RequestBody CreateSurveyDTO createSurveyDTO) {
        try {
            String userId = SessionContext.getCurrentUser();
            Survey survey = surveyManageService.createSurveyWithDetails(createSurveyDTO, userId);
            return ResponseEntity.ok().body(Collections.singletonMap("surveyId", survey.getSurveyId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{surveyId}")
    public ResponseEntity<Object> deleteSurvey(@PathVariable("surveyId") Long surveyId) {
        if(surveyManageService.deleteSurvey(surveyId)) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.badRequest().body(Collections.singletonMap("errorMessage", "해당 아이디의 설문이 존재하지 않습니다."));
    }
}
