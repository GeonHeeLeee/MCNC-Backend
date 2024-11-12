package mcnc.survwey.api.survey.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.SurveyDTO;
import mcnc.survwey.api.survey.dto.SurveyWithCountDTO;
import mcnc.survwey.api.survey.dto.SurveyWithDetailDTO;
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
@RequestMapping("/inquiry")
public class SurveyInquiryController {

    private final SurveyInquiryService surveyInquiryService;

    /**
     * 사용자가 생성한 설문 목록 조회
     *
     * @param pageable
     * @return
     */
    @GetMapping("/created")
    public ResponseEntity<Page<SurveyWithCountDTO>> inquiryUserCreatedSurveyList(@PageableDefault(size = 10) Pageable pageable) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyWithCountDTO> userCreatedSurveyList = surveyInquiryService.getUserCreatedSurveyList(userId, pageable);
        return ResponseEntity.ok(userCreatedSurveyList);
    }

    /**
     * 사용자가 응답한 설문 목록 조회
     *
     * @param pageable
     * @return
     */
    @GetMapping("/respond")
    public ResponseEntity<Object> inquiryUserRespondSurveyList(@PageableDefault(size = 10) Pageable pageable) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyDTO> userRespondSurveyList = surveyInquiryService.getUserRespondSurveyList(userId, pageable);
        return ResponseEntity.ok(userRespondSurveyList);
    }

    @GetMapping("/detail/{surveyId}")
    public ResponseEntity<Object> inquirySurveyWithDetail(@PathVariable("surveyId") Long surveyId) {
        SurveyWithDetailDTO surveyWithDetailDTO = surveyInquiryService.getSurveyWithDetail(surveyId);
        return ResponseEntity.ok(surveyWithDetailDTO);
    }
}
