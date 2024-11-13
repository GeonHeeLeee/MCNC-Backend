package mcnc.survwey.domain.survey.inquiry.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyInfoDTO;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.inquiry.service.SurveyInquiryService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey/inquiry")
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

    /**
     * 설문 응답을 위한 설문, 질문, 보기 조회
     *
     * @param surveyId
     * @return
     */
    @GetMapping("/detail/{surveyId}")
    public ResponseEntity<Object> inquirySurveyWithDetail(@PathVariable("surveyId") Long surveyId) {
        SurveyWithDetailDTO surveyWithDetailDTO = surveyInquiryService.getSurveyWithDetail(surveyId);
        return ResponseEntity.ok(surveyWithDetailDTO);
    }

    /**
     * 사용자가 특정 키워드로 설문을 찾기 위해 설문 조회
     *
     * @param title, pageable
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<Object> surveySearch(@RequestParam String title, @PageableDefault(size = 10) Pageable pageable) {
        String userId = SessionContext.getCurrentUser();
        log.info(title);
        Page<Survey> surveys = surveyInquiryService.surveySearch(userId, title, pageable);
        Page<SurveyInfoDTO> surveyDTOs = surveys.map(SurveyInfoDTO::of);

        return ResponseEntity.ok(surveyDTOs);
    }
}
