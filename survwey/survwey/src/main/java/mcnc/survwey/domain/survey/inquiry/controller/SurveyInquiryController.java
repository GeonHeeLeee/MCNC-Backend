package mcnc.survwey.domain.survey.inquiry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.inquiry.dto.SearchDTO;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/survey/inquiry")
@Tag(name = "설문 조회", description = "응답/생성한 설문 리스트 조회, 상세 조회 및 검색 API")
public class SurveyInquiryController {

    private final SurveyInquiryService surveyInquiryService;

    /**
     * 사용자가 생성한 설문 목록 조회
     *
     * @param pageable
     * @return
     */
    @GetMapping("/created")
    @Operation(summary = "사용자 본인이 생성한 설문 리스트 조회", description = "쿼리 파라미터 형식으로 size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    public ResponseEntity<Page<SurveyWithCountDTO>> inquiryUserCreatedSurveyList(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyWithCountDTO> userCreatedSurveyList = surveyInquiryService.getUserCreatedSurveyList(userId, page, size);
        return ResponseEntity.ok(userCreatedSurveyList);
    }

    /**
     * 사용자가 응답한 설문 목록 조회
     *
     * @param pageable
     * @return
     */
    @GetMapping("/respond")
    @Operation(summary = "사용자 본인이 응답한 설문 리스트 조회", description = "쿼리 파라미터 형식으로 size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    public ResponseEntity<Page<SurveyDTO>> inquiryUserRespondSurveyList(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyDTO> userRespondSurveyList = surveyInquiryService.getUserRespondSurveyList(userId, page, size);
        return ResponseEntity.ok(userRespondSurveyList);
    }

    /**
     * 설문 응답을 위한 설문, 질문, 보기 조회
     *
     * @param surveyId
     * @return
     */
    @GetMapping("/detail/{surveyId}")
    @Operation(summary = "특정 설문/질문/보기 조회", description = "surveyId(설문 아이디)로 조회")
    public ResponseEntity<SurveyWithDetailDTO> inquirySurveyWithDetail(@PathVariable("surveyId") Long surveyId) {
        SurveyWithDetailDTO surveyWithDetailDTO = surveyInquiryService.getSurveyWithDetail(surveyId);
        return ResponseEntity.ok(surveyWithDetailDTO);
    }

    @PostMapping("/search")
    public ResponseEntity<List<SurveyInfoDTO>> surveySearch(@RequestBody SearchDTO searchDTO) {
        log.info("Received title: {}", searchDTO.getTitle());
        List<Survey> surveys = surveyInquiryService.surveySearch(searchDTO);
        List<SurveyInfoDTO> surveyDTOs = surveys.stream().map(SurveyInfoDTO::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(surveyDTOs);
    }
}
