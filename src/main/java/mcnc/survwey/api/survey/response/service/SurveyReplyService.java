package mcnc.survwey.api.survey.response.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import mcnc.survwey.domain.objAnswer.repository.ObjAnswerRepository;
import mcnc.survwey.domain.objAnswer.service.ObjAnswerService;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.respond.repository.RespondRepository;
import mcnc.survwey.domain.respond.service.RespondService;
import mcnc.survwey.domain.subjAnswer.SubjAnswer;
import mcnc.survwey.domain.subjAnswer.repository.SubjAnswerRepository;
import mcnc.survwey.domain.subjAnswer.service.SubjAnswerService;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.service.SurveyService;
import mcnc.survwey.api.survey.response.dto.reply.SurveyReplyDTO;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyReplyService {

    private final SurveyService surveyService;
    private final UserService userService;
    private final RespondService respondService;
    private final RespondRepository respondRepository;
    private final ObjAnswerRepository objAnswerRepository;
    private final SubjAnswerRepository subjAnswerRepository;
    private final ObjAnswerService objAnswerService;
    private final SubjAnswerService subjAnswerService;


    /**
     * 설문 응답 저장
     *
     * @param surveyReplyDTO
     * @param userId         - 응답자가 DB에 저장되지 않은 경우 에러
     *                       - 해당 설문 ID가 없는 경우 에러
     *                       - 이미 설문에 응답했으면 에러
     *                       - 요청 질문이 설문의 질문 아이디와 일치하지 않으면 에러
     *                       - 이미 만료된 설문이면 에러
     * @Author 이건희
     */
    @Transactional
    public void saveSurveyReply(SurveyReplyDTO surveyReplyDTO, String userId) {
        User respondedUser = userService.findByUserId(userId);
        Survey respondedSurvey = surveyService.findBySurveyId(surveyReplyDTO.getSurveyId());

        //이미 응답했으면 에러 전송
        if (respondService.isUserRespondedToSurvey(surveyReplyDTO.getSurveyId(), userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.HAS_ALREADY_RESPOND_TO_SURVEY);
        }

        //요청 질문과 설문이 일치하는지 확인
        validateQuestionInputForSurvey(surveyReplyDTO, respondedSurvey);
        //설문이 만료되었는지 확인
        surveyService.checkSurveyExpiration(respondedSurvey.getExpireDate());

        List<SurveyReplyDTO.ReplyResponseDTO> responseList = surveyReplyDTO.getResponseList();
        //객관식, 주관식 응답 생성
        List<ObjAnswer> objAnswerList = objAnswerService.createObjectiveAnswers(responseList, respondedUser);
        List<SubjAnswer> subjAnswerList = subjAnswerService.createSubjectiveAnswers(responseList, respondedUser);

        //저장
        respondRepository.save(new Respond(respondedUser, respondedSurvey));
        subjAnswerRepository.saveAll(subjAnswerList);
        objAnswerRepository.saveAll(objAnswerList);
    }

    /**
     * 요청 질문의 아이디가 설문 질문의 아이디와 일치하는지 확인
     * - 일치하지 않으면 에러 전송
     *
     * @param surveyReplyDTO
     * @param respondedSurvey
     * @Author 이건희
     */
    private void validateQuestionInputForSurvey(SurveyReplyDTO surveyReplyDTO, Survey respondedSurvey) {
        //질문 ID 집합
        Set<Long> inputQuestionSet = respondedSurvey.getQuestionList()
                .stream().map(Question::getQuesId).collect(Collectors.toSet());
        //응답 ID 집합
        Set<Long> existingQuestionSet = surveyReplyDTO.getResponseList()
                .stream().map(SurveyReplyDTO.ReplyResponseDTO::getQuesId).collect(Collectors.toSet());

        if (!inputQuestionSet.equals(existingQuestionSet)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.QUESTION_NOT_MATCH_TO_SURVEY);
        }
    }
}
