package mcnc.survwey.api.survey.inquiry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithDateDTO;
import mcnc.survwey.api.survey.inquiry.service.SurveyInquiryService;
import mcnc.survwey.api.survey.manage.dto.SurveyWithDetailDTO;
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
    public ResponseEntity<Page<SurveyWithCountDTO>> getUserCreatedSurveyList(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                             @RequestParam(name = "size", defaultValue = "10") int size) {
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
    public ResponseEntity<Page<SurveyWithDateDTO>> getUserRespondSurveyList(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyWithDateDTO> userRespondSurveyList = surveyInquiryService.getUserRespondSurveyList(userId, page, size);
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
     *
     * @param title
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search")
    @Operation(summary = "내가 참여할 수 있는 설문 검색", description = "쿼리 파라미터 형식으로 title(검색할 키워드), size(페이지 당 개수), page(페이지 번호)를 주면 페이지네이션으로 처리됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 검색 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 인증을 하지 않음")
    })

    public ResponseEntity<Page<SurveyDTO>> searchSurveyToParticipate(@RequestParam(name = "title") String title,
                                                                     @RequestParam(name = "page") int page,
                                                                     @RequestParam(name = "size") int size) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyDTO> surveyDTOList = surveyInquiryService.searchAvailableSurvey(title, userId, page, size);
        return ResponseEntity.ok(surveyDTOList);
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
    public ResponseEntity<Page<SurveyWithCountDTO>> searchUserCreatedSurvey(@RequestParam(name = "title") String title,
                                                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                                                   @RequestParam(name = "size", defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyWithCountDTO> surveyWithCountDTOPage = surveyInquiryService.searchUserCreatedSurvey(userId, title, page, size);
        return ResponseEntity.ok(surveyWithCountDTOPage);
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
    public ResponseEntity<Page<SurveyWithDateDTO>> searchRespondedSurveys(@RequestParam(name = "title") String title,
                                                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        String userId = SessionContext.getCurrentUser();
        Page<SurveyWithDateDTO> surveyWithDateDTOPage = surveyInquiryService.searchRespondedSurveys(userId, title, page, size);
        return ResponseEntity.ok(surveyWithDateDTOPage);
    }

}
