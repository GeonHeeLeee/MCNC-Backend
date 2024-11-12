package mcnc.survwey.domain.survey;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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


    public boolean deleteSurveyById(Long surveyId) {
        if (surveyRepository.existsById(surveyId)) {
            surveyRepository.deleteById(surveyId);
            return true;
        } else {
            return false;
        }
    }

    public Survey findBySurveyId(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
    }

    public List<Survey> findByUser_UserId(String userId) {
        return surveyRepository.findByUser_UserId(userId);
    }

}
