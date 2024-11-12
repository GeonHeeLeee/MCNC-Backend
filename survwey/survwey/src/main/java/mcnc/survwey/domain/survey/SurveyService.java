package mcnc.survwey.domain.survey;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.dto.SurveyModifyDTO;
import mcnc.survwey.api.survey.dto.SurveyWithDetailDTO;
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

    public Survey initializeSurvey(SurveyWithDetailDTO surveyWithDetailDTO, User creator) {
        Survey createdSurvey = Survey.builder()
                .title(surveyWithDetailDTO.getTitle())
                .expireDate(surveyWithDetailDTO.getExpireDate())
                .description(surveyWithDetailDTO.getDescription())
                .user(creator)
                .createDate(LocalDateTime.now())
                .build();
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

    public List<Survey> findByUser_UserId(String userId) {
        return surveyRepository.findByUser_UserId(userId);
    }

    public Survey findBySurveyId(Long surveyId) {
        return surveyRepository.findBySurveyId(surveyId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE));
        //임시 error 코드
    }

    public Survey findBySurveyIdAndUserId(Long surveyId, String userId) {
        return surveyRepository.findBySurveyIdAndUser_UserId(surveyId, userId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PASSWORD));
        //임시 error 코드
    }

}
