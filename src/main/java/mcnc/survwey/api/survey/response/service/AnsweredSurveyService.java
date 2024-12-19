package mcnc.survwey.api.survey.response.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.response.dto.answered.AnsweredQuestionDTO;
import mcnc.survwey.api.survey.response.dto.answered.AnsweredSurveyDTO;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import mcnc.survwey.domain.objAnswer.repository.ObjAnswerRepository;
import mcnc.survwey.domain.subjAnswer.repository.SubjAnswerRepository;
import mcnc.survwey.domain.survey.repository.SurveyRepository;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static mcnc.survwey.domain.subjAnswer.QSubjAnswer.subjAnswer;

@Service
@RequiredArgsConstructor
public class AnsweredSurveyService {

    private final SurveyRepository surveyRepository;
    private final ObjAnswerRepository objAnswerRepository;
    private final SubjAnswerRepository subjAnswerRepository;


    /**
     * 사용자가 응답한 설문 조회
     * - 설문 기본 정보 조회 후 객관식, 주관식 테이블 조회하여 추가
     * - 캐싱 적용
     * @param surveyId
     * @param userId
     * @return - 해당 사용자가 응답하지 않은 설문이면 에러 전송
     * - 해당 아이디의 설문이 존재하지 않으면 에러 전송
     */
    @Cacheable(value = "survey")
    @Transactional(readOnly = true)
    public AnsweredSurveyDTO getUserAnsweredSurvey(Long surveyId, String userId) {
        //요청 ID의 설문이 존재하지 않으면 에러 전송, 아닐 시 응답 DTO 객체 생성
        AnsweredSurveyDTO answeredSurveyDTO = Optional.ofNullable(surveyRepository.findSurveyWithDetail(surveyId))
                .map(AnsweredSurveyDTO::of)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));

        //객관식 응답 Map 생성
        Map<Object, List<ObjAnswer>> objAnswerMap = getObjAnswerMap(surveyId, userId);
        //주관식 응답 Map 생성
        Map<Long, String> subjAnswerMap = getSubjAnswerMap(surveyId, userId);

        //질문에 생성한 주관식, 객관식 응답 할당
        answeredSurveyDTO.getQuestionList()
                .forEach(question -> assignAnswersToQuestion(question, objAnswerMap, subjAnswerMap));

        return answeredSurveyDTO;
    }

    /**
     * 질문에 주관식, 객관식 응답 할당 메서드
     * - 다중 객관식일 경우 반복문을 돌면서 사용자가 선택한 보기(순서) 추가
     * - 기타 응답이 있을 경우 기타 응답 추가
     * - 단일 객관식일 경우, 사용자가 선택한 보기(순서) 추가
     * - 기타 응답이 있을 경우 기타 응답 추가
     * - 주관식일 경우 주관식 응답 추가
     *
     * @param question
     * @param objAnswerMap
     * @param subjAnswerMap - 만약 질문 타입이 3개중 해당 되지 않으면 유효하지 않은 질문 타입 에러 전송
     */
    private void assignAnswersToQuestion(AnsweredQuestionDTO question, Map<Object, List<ObjAnswer>> objAnswerMap, Map<Long, String> subjAnswerMap) {
        long quesId = question.getQuesId();
        //해당 질문에 응답을 하지 않았을 경우
        if (!objAnswerMap.containsKey(quesId) && !subjAnswerMap.containsKey(quesId)) {
            return;
        }
        //해당하는 질문 유형에 따라 추가 없을 경우 에러 코드와 메시지 발송
        switch (question.getQuestionType()) {
            case OBJ_MULTI:
                objAnswerMap.get(quesId).forEach(objAnswer -> {
                    addObjectiveResponse(question, objAnswer);
                });
                break;

            case OBJ_SINGLE:
                ObjAnswer objAnswer = objAnswerMap.get(quesId).get(0);
                addObjectiveResponse(question, objAnswer);
                break;

            case SUBJECTIVE:
                question.setSubjAnswer(subjAnswerMap.get(quesId));
                break;

            default:
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INVALID_QUESTION_TYPE);
        }
    }

    /**
     * 객관식 응답 추가
     * @param question
     * @param objAnswer
     */
    private void addObjectiveResponse(AnsweredQuestionDTO question, ObjAnswer objAnswer) {
        int sequence = objAnswer.getSelection().getId().getSequence();
        question.getObjAnswerList().add(sequence);
        //기타 응답일 경우
        setEtcAnswerIfPresent(question, objAnswer);
    }

    /**
     * 기타 응답이 존재할 시 추가하는 메서드
     *
     * @param question
     * @param objAnswer
     */
    private void setEtcAnswerIfPresent(AnsweredQuestionDTO question, ObjAnswer objAnswer) {
        if (objAnswer.getEtcAnswer() != null && !objAnswer.getEtcAnswer().isEmpty()) {
            question.setEtcAnswer(objAnswer.getEtcAnswer());
        }
    }

    /**
     * 객관식 응답 조회 후 Map으로 반환
     * - 질문 아이디(QuesId)를 Key로, ObjAnswer 엔티티를 Value로 가지는 Map 반환
     *
     * @param surveyId
     * @param userId
     * @return
     */
    private Map<Object, List<ObjAnswer>> getObjAnswerMap(Long surveyId, String userId) {
        //QuesId를 키로 가지도록
        return objAnswerRepository.findUserRespondedAnswer(surveyId, userId)
                .stream()
                .collect(Collectors.groupingBy(
                        answer -> answer.getSelection().getQuestion().getQuesId()
                ));
    }

    /**
     * 주관식 응답 조회 후 Map으로 반환
     * - 질문 아이디(QuesId)를 Key로, 객관식 응답을 Value로 가지는 Map 반환
     *
     * @param surveyId
     * @param userId
     * @return
     */
    private Map<Long, String> getSubjAnswerMap(Long surveyId, String userId) {
        //QuesId를 키로 가지도록
        return subjAnswerRepository.findUserRespondedAnswer(surveyId, userId)
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(subjAnswer.question.quesId),
                        tuple -> tuple.get(subjAnswer.response)
                ));
    }
}
