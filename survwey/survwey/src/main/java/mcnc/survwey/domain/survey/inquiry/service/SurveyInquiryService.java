package mcnc.survwey.domain.survey.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyInquiryService {

    private final SurveyRepository surveyRepository;

    public Page<SurveyWithCountDTO> getUserCreatedSurveyList(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> surveyPageList = surveyRepository.findSurveyListWithRespondCountByUserId(userId, pageable);
        return surveyPageList.map(SurveyWithCountDTO::of);
    }


    public Page<SurveyDTO> getUserRespondSurveyList(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findRespondedSurveyByUserId(userId, pageable);
    }

    public SurveyWithDetailDTO getSurveyWithDetail(Long surveyId) {
        return Optional.ofNullable(surveyRepository.getSurveyWithDetail(surveyId))
                .map(SurveyWithDetailDTO::of)
                .orElse(null);
    }

    /**
     * 설문 검색
     *
     * @param userId
     * @param title
     * @param page
     * @param size
     * @return
     */
    public Page<Survey> surveySearch(String userId, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findByUser_UserIdAndTitleContainingIgnoreCase(userId, title, pageable);
    }

    public Page<Survey> respondedSurveySearch(String userId, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findSurveysUserHasRespondedTo(userId, title, pageable);
    }


}
