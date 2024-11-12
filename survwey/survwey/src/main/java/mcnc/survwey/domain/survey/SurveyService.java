package mcnc.survwey.domain.survey;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
