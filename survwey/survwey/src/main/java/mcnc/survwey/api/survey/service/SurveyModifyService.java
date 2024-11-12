package mcnc.survwey.api.survey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.SurveyWithDetailDTO;
import mcnc.survwey.api.survey.dto.SurveyModifyDTO;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.QuestionService;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.selection.SelectionService;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.SurveyRepository;
import mcnc.survwey.domain.survey.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.UserRepository;
import mcnc.survwey.domain.user.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyModifyService {

    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final SelectionService selectionService;
    private final QuestionService questionService;
    private final SurveyService surveyService;

    @Transactional
    public void surveyModify(SurveyModifyDTO surveyModifyDTO) {

        User user = userRepository.findById(surveyModifyDTO.getUserId())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_NOT_FOUND_BY_ID));

//        Survey survey = surveyRepository.findByIdAndUser_Id(surveyModifyDTO.getId(), user.getUserId())
//                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE));


//        Respond respond =
        //응답한 사용자 수가 0보다 크면 X
//        if(list.size() > 0){
            //error
//        }

//        CreateSurveyDTO.builder()
//                .title(m)

//        Survey modifySurvey = surveyService.initializeSurvey(surveyModifyDTO, user);

//        surveyModifyDTO.getQuestionList()
//                .forEach(questionDTO -> {
//                    Question createdQuestion = questionService.buildAndSaveQuestion(questionDTO, survey);
//                    selectionService.addSelectionsToQuestion(createdQuestion, questionDTO.getSelectionList());
//                });
//        return survey;
    }

}
