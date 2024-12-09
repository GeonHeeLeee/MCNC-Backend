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
    public SurveyWithDetailDTO findSurveyWithDetail(Long surveyId) {
        return Optional.ofNullable(surveyRepository.findSurveyWithDetail(surveyId))
                .map(SurveyWithDetailDTO::of)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
    }

    /**
     * 검색 시 제목의 공백 제거
     * @param title
     * @return
     */
    private String removeTitleSpaces(String title) {
        return title.replaceAll("\\s", "");
    }

    /**
     * 참여 가능한 설문 검색
     * @param title
     * @param page
     * @param size
     * @return
     */
    public Page<SurveyDTO> searchAvailableSurvey(String title, String userId, int page, int size) {
        title = removeTitleSpaces(title);
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findAvailableSurvey(title, userId, pageable);
    }

    /**
     * 사용자가 응답한 설문 검색
     * @param userId
     * @param title
     * @param page
     * @param size
     * @return
     */
    public Page<Survey> searchRespondedSurveys(String userId, String title, int page, int size) {
        title = removeTitleSpaces(title);
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findSurveysUserHasRespondedTo(userId, title, pageable);
    }

    /**
     * 사용자가 생성한 설문 검색
     * @param userId
     * @param title
     * @param page
     * @param size
     * @return
     */
    public Page<Survey> searchUserCreatedSurvey(String userId, String title, int page, int size) {
        title = removeTitleSpaces(title);
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findByUser_UserIdAndTitleContainingIgnoreCaseOrderByCreateDateDesc(userId, title, pageable);
    }

}
