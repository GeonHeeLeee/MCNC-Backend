package mcnc.survwey.domain.survey.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyInquiryService {

    private final SurveyRepository surveyRepository;

    public Page<SurveyWithCountDTO> getUserCreatedSurveyList(String userId, Pageable pageable) {
        Page<Object[]> surveyPageList = surveyRepository.findSurveyListWithRespondCountByUserId(userId, pageable);
        return surveyPageList.map(SurveyWithCountDTO::of);
    }


    public Page<SurveyDTO> getUserRespondSurveyList(String userId, Pageable pageable) {
        return surveyRepository.findRespondedSurveyByUserId(userId, pageable);
    }

    public SurveyWithDetailDTO getSurveyWithDetail(Long surveyId) {
        return Optional.ofNullable(surveyRepository.getSurveyWithDetail(surveyId))
                .map(SurveyWithDetailDTO::of)
                .orElse(null);
    }

    /**
     * 설문 검색
     * @param title, pageable
     * @return
     */
    public Page<Survey> surveySearch(String userId, String title, Pageable pageable){
        return surveyRepository.findByUser_UserIdAndTitleContainingIgnoreCase(userId, title, pageable);
    }

}
