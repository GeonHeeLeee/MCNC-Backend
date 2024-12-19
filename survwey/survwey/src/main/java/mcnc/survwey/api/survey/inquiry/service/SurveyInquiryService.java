package mcnc.survwey.api.survey.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithDateDTO;
import mcnc.survwey.api.survey.manage.dto.SurveyWithDetailDTO;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
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
    public Page<SurveyWithDateDTO> getUserRespondSurveyList(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findRespondedSurveyListByUserId(userId, pageable);
    }

    /**
     * 특정 설문 조회
     *
     * @param surveyId
     * @return - 해당 Id의 설문이 없을 시, 오류 발생
     */
    public SurveyWithDetailDTO getSurveyWithDetail(Long surveyId) {
        //설문 id로 설문 정보 조회 후 결과가 있으면  SurveyWithDetailDto 반환, null이면 에러코드
        return Optional.ofNullable(surveyRepository.findSurveyWithDetail(surveyId))
                .map(SurveyWithDetailDTO::of)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
    }

    /**
     * 검색 시 제목의 공백 제거
     *
     * @param title
     * @return
     */
    private String removeTitleSpaces(String title) {
        return title.replaceAll("\\s", "");
    }

    /**
     * 참여 가능한 설문 검색
     *
     * @param title
     * @param page
     * @param size
     * @return
     */
    public Page<SurveyDTO> searchAvailableSurvey(String title, String userId, int page, int size) {
        title = removeTitleSpaces(title);
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findAvailableSurveyList(title, userId, pageable);
    }

    /**
     * 사용자가 응답한 설문 검색
     *
     * @param userId
     * @param title
     * @param page
     * @param size
     * @return
     */
    public Page<SurveyWithDateDTO> searchRespondedSurveys(String userId, String title, int page, int size) {
        title = removeTitleSpaces(title);
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findRespondedSurveyListByTitleAndUserId(title, userId, pageable);
    }

    /**
     * 사용자가 생성한 설문 검색
     *
     * @param userId
     * @param title
     * @param page
     * @param size
     * @return
     */
    public Page<SurveyWithCountDTO> searchUserCreatedSurvey(String userId, String title, int page, int size) {
        title = removeTitleSpaces(title);
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findUserCreatedSurveyListByTitleAndUserId(title, userId, pageable);
    }
}