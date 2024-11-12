package mcnc.survwey.api.survey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.SurveyDTO;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.QuestionService;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.respond.RespondService;
import mcnc.survwey.domain.selection.SelectionService;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyModifyService {

    private final UserService userService;
    private final SelectionService selectionService;
    private final QuestionService questionService;
    private final SurveyService surveyService;
    private final RespondService respondService;

//    @Transactional
//    public Survey surveyModifyWithDetails(SurveyDTO surveyDTO, Long surveyId, String userId) {
//
//
//        return updatedSurvey;
//    }

}
