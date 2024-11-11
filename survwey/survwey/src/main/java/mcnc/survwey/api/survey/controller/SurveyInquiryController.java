package mcnc.survwey.api.survey.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.SurveyDTO;
import mcnc.survwey.api.survey.dto.SurveyWithCountDTO;
import mcnc.survwey.api.survey.service.SurveyInquiryService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class SurveyInquiryController {

    private final SurveyInquiryService surveyInquiryService;

    @GetMapping("/inquiry/created")
    public ResponseEntity<Page<SurveyWithCountDTO>> inquiryUserCreatedSurveyList(@PageableDefault(size = 10) Pageable pageable) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyWithCountDTO> userCreatedSurveyList = surveyInquiryService.getUserCreatedSurveyList(userId, pageable);
        return ResponseEntity.ok(userCreatedSurveyList);
    }

    @GetMapping("/inquiry/respond")
    public ResponseEntity<Object> inquiryUserRespondSurveyList(@PageableDefault(size = 10) Pageable pageable) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyDTO> userRespondSurveyList = surveyInquiryService.getUserRespondSurveyList(userId, pageable);
        return ResponseEntity.ok(userRespondSurveyList);
    }

}
