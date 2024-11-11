package mcnc.survwey.api.survey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.SurveyDTO;
import mcnc.survwey.api.survey.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.SurveyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


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
}
