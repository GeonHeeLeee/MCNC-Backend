package mcnc.survwey.domain.survey.common.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;

    public Survey buildAndSaveSurvey(SurveyWithDetailDTO surveyWithDetailDTO, User creator) {
        Survey createdSurvey = surveyWithDetailDTO.toEntity(creator);
        surveyRepository.save(createdSurvey);
        return createdSurvey;
    }


    public Survey findBySurveyId(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
    }

    public void checkSurveyExpiration(LocalDateTime expireDate) {
        if (expireDate.isBefore(LocalDateTime.now())
                || expireDate.isEqual(LocalDateTime.now())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EXPIRED_SURVEY);
        }
    }

    public void verifyUserMadeSurvey(String userId, Survey survey) {
        if (!survey.getUser().getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, ErrorCode.SURVEY_CREATOR_NOT_MATCH);
        }
    }
}
