package mcnc.survwey.api.survey.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.manage.dto.SurveyWithDetailDTO;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
import mcnc.survwey.domain.survey.service.SurveyService;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.repository.SurveyRepository;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyInquiryService {

    private final SurveyRepository surveyRepository;
    private final SurveyService surveyService;

    /**
     * 사용자가 생성한 설문 리스트 조회
     * - 페이지네이션 적용
     *
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public Page<SurveyWithCountDTO> getUserCreatedSurveyList(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findSurveyListWithRespondCountByUserId(userId, pageable);
    }


    /**
     * 사용자가 응답한 설문 리스트 조회
     * - 페이지네이션 적용
     *
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public Page<SurveyDTO> getUserRespondSurveyList(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findRespondedSurveyByUserId(userId, pageable);
    }

    /**
     * 특정 설문 조회
     *
     * @param surveyId
     * @return - 해당 Id의 설문이 없을 시, 오류 발생
     */
    public SurveyWithDetailDTO getSurveyWithDetail(Long surveyId) {
        return Optional.ofNullable(surveyRepository.findSurveyWithDetail(surveyId))
                .map(SurveyWithDetailDTO::of)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
    }

    /**
     * 설문 검색
     *
     * @param title
     * @param page
     * @param size
     * @return
     */
    public Page<SurveyDTO> searchSurveyToParticipate(String title, String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findSurveyThatCanParticipate(title, userId, pageable);
    }

    public Page<Survey> searchRespondedSurveys(String userId, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findSurveysUserHasRespondedTo(userId, title, pageable);
    }

    public Page<Survey> searchUserCreatedSurvey(String userId, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findByUser_UserIdAndTitleContainingIgnoreCaseOrderByCreateDateDesc(userId, title, pageable);
    }


    /**
     * 본인이 만든 설문인지 확인
     * - 본인이 만든 설문이면 result : true
     * - 본인이 만든 설문이 아니면 result : false
     *
     * @param userId
     * @param surveyId
     * @return - 에러: 설문이 존재하지 않음
     */
    public Map<String, Boolean> isSurveyUserMade(String userId, Long surveyId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        Map<String, Boolean> response = new HashMap<>();
        if (survey.getUser().getUserId().equals(userId)) {
            response.put("result", true);
        } else {
            response.put("result", false);
        }
        return response;
    }
}
