package mcnc.survwey.api.survey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.respond.RespondService;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyModifyService {

    private final UserService userService;;
    private final RespondService respondService;
    private final SurveyManageService surveyManageService;

    @Transactional
    public Survey surveyModifyWithDetails(SurveyWithDetailDTO surveyWithDetailDTO, String userId) {
        User updater = userService.findByUserId(userId);
        //변경할 설문
        respondService.existsBySurveyId(surveyWithDetailDTO.getSurveyId());
        //설문 응답자가 존재하면 error

        log.info(surveyWithDetailDTO.toString());
        if (surveyManageService.deleteSurvey(surveyWithDetailDTO.getSurveyId())) {
            Survey updatedSurvey = surveyManageService.createSurveyWithDetails(surveyWithDetailDTO, userId);
            return updatedSurvey;
            //asd
        } else {
            return null;
        }

    }

}
