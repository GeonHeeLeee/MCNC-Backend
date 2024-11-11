package mcnc.survwey.api.survey.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.UserCreatedSurveyDTO;
import mcnc.survwey.api.survey.service.SurveyInquiryService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class SurveyInquiryController {

    private final SurveyInquiryService surveyInquiryService;

    @GetMapping("/inquiry/created")
    public ResponseEntity<Map<String, List<UserCreatedSurveyDTO>>> inquiryUserCreatedSurveyList() {
        String userId = SessionContext.getCurrentUser();
        Map<String, List<UserCreatedSurveyDTO>> userCreatedSurveyList = surveyInquiryService.getUserCreatedSurveyList(userId);
        return ResponseEntity.ok(userCreatedSurveyList);
    }

}
