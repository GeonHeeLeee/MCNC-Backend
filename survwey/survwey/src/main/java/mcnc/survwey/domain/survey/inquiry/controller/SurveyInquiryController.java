package mcnc.survwey.domain.survey.inquiry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.inquiry.service.SurveyInquiryService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
     * @param
     * @return
     */
    @GetMapping("/created")
    @Operation(summary = "사용자 본인이 생성한 설문 리스트 조회", description = "쿼리 파라미터 형식으로 size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자가 생성한 설문 리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Page<SurveyWithCountDTO>> getUserCreatedSurveyList(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyWithCountDTO> userCreatedSurveyList = surveyInquiryService.getUserCreatedSurveyList(userId, page, size);
        return ResponseEntity.ok(userCreatedSurveyList);
    }

    /**
     * 사용자가 응답한 설문 목록 조회
     *
     * @param
     * @return
     */
    @GetMapping("/respond")
    @Operation(summary = "사용자 본인이 응답한 설문 리스트 조회", description = "쿼리 파라미터 형식으로 size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자가 응답한 설문 리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Page<SurveyDTO>> getUserRespondSurveyList(@RequestParam(defaultValue = "0") int page,
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "특정 설문 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<SurveyWithDetailDTO> getSurveyWithDetail(@PathVariable("surveyId") Long surveyId) {
        SurveyWithDetailDTO surveyWithDetailDTO = surveyInquiryService.getSurveyWithDetail(surveyId);
        return ResponseEntity.ok(surveyWithDetailDTO);
    }

    /**
     * 설문 특정 키워드로 검색
     * @param title
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search")
    @Operation(summary = "설문 전체 검색", description = "쿼리 파라미터 형식으로 title(검색할 키워드), size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 검색 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Page<SurveyDTO>> searchSurveys(@RequestParam String title,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Page<Survey> surveys = surveyInquiryService.searchSurveys(title, page, size);
        //전체 설문에서 검색
        Page<SurveyDTO> surveyInfoDTOS = surveys.map(SurveyDTO::of);
        return ResponseEntity.ok(surveyInfoDTOS);
    }

    /**
     * 사용자가 특정키워드로 본인이 생성한 설문 검색
     *
     * @param title
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search/created")
    @Operation(summary = "사용자 본인이 생성한 설문 검색", description = "쿼리 파라미터 형식으로 title(검색할 키워드), size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성한 설문 검색 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Page<SurveyDTO>> searchUserCreatedSurvey(@RequestParam String title,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();

        Page<Survey> surveys = surveyInquiryService.searchUserCreatedSurvey(userId, title, page, size);
        //생성한 설문에서 검색
        Page<SurveyDTO> surveyDTOs = surveys.map(SurveyDTO::of);
        return ResponseEntity.ok(surveyDTOs);
    }

    /**
     * 사용자가 특정 키워드로 참여한 설문 검색
     *
     * @param title
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search/respond")
    @Operation(summary = "사용자 본인이 참여한 설문 검색", description = "쿼리 파라미터 형식으로 title(검색할 키워드), size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참여한 설문 검색 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })
    public ResponseEntity<Page<SurveyDTO>> searchRespondedSurveys(@RequestParam String title,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();

        Page<Survey> surveys = surveyInquiryService.searchRespondedSurveys(userId, title, page, size);
        //참여한 설문에서 검색
        Page<SurveyDTO> surveyInfoDTOS = surveys.map(SurveyDTO::of);
        return ResponseEntity.ok(surveyInfoDTOS);
    }


}
