package mcnc.survwey.domain.survey.response.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import mcnc.survwey.domain.objAnswer.repository.ObjAnswerRepository;
import mcnc.survwey.domain.objAnswer.service.ObjAnswerService;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.dto.QuestionResponseDTO;
import mcnc.survwey.domain.question.dto.QuestionResultDTO;
import mcnc.survwey.domain.question.dto.SurveyResultMapper;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.respond.dto.ResponseDTO;
import mcnc.survwey.domain.respond.repository.RespondRepository;
import mcnc.survwey.domain.respond.service.RespondService;
import mcnc.survwey.domain.selection.dto.SelectionResultDTO;
import mcnc.survwey.domain.subjAnswer.SubjAnswer;
import mcnc.survwey.domain.subjAnswer.repository.SubjAnswerRepository;
import mcnc.survwey.domain.subjAnswer.service.SubjAnswerService;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.survey.response.dto.SurveyResponseDTO;
import mcnc.survwey.domain.survey.response.dto.SurveyResultDTO;
import mcnc.survwey.domain.survey.response.dto.SurveyReplyDTO;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.AgeCountDTO;
import mcnc.survwey.domain.user.dto.GenderCountDTO;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static mcnc.survwey.domain.subjAnswer.QSubjAnswer.subjAnswer;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyResponseService {

    private final SurveyService surveyService;
    private final UserService userService;
    private final RespondService respondService;
    private final RespondRepository respondRepository;
    private final ObjAnswerRepository objAnswerRepository;
    private final SubjAnswerRepository subjAnswerRepository;
    private final QuestionRepository questionRepository;
    private final ObjAnswerService objAnswerService;
    private final SubjAnswerService subjAnswerService;
    private final SurveyRepository surveyRepository;

    /**
     * 설문 응답 저장
     *
     * @param surveyReplyDTO
     * @param userId         - 응답자가 DB에 저장되지 않은 경우 에러
     *                       - 해당 설문 ID가 없는 경우 에러
     *                       - 이미 설문에 응답했으면 에러
     *                       - 요청 질문이 설문의 질문 아이디와 일치하지 않으면 에러
     *                       - 이미 만료된 설문이면 에러
     */
    @Transactional
    public void saveSurveyReply(SurveyReplyDTO surveyReplyDTO, String userId) {
        User respondedUser = userService.findByUserId(userId);
        Survey respondedSurvey = surveyService.findBySurveyId(surveyReplyDTO.getSurveyId());

        //이미 응답했으면 에러 전송
        if (respondService.hasUserRespondedToSurvey(surveyReplyDTO.getSurveyId(), userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.HAS_ALREADY_RESPOND_TO_SURVEY);
        }

        //요청 질문과 설문이 일치하는지 확인
        validateQuestionInputForSurvey(surveyReplyDTO, respondedSurvey);
        //설문이 만료되었는지 확인
        surveyService.checkSurveyExpiration(respondedSurvey.getExpireDate());

        List<ResponseDTO> responseList = surveyReplyDTO.getResponseList();
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
     */
    private void validateQuestionInputForSurvey(SurveyReplyDTO surveyReplyDTO, Survey respondedSurvey) {
        Set<Long> inputQuestionSet = respondedSurvey.getQuestionList()
                .stream().map(Question::getQuesId).collect(Collectors.toSet());

        Set<Long> existingQuestionSet = surveyReplyDTO.getResponseList()
                .stream().map(ResponseDTO::getQuesId).collect(Collectors.toSet());

        if (!inputQuestionSet.equals(existingQuestionSet)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.QUESTION_NOT_MATCH_TO_SURVEY);
        }
    }


    /**
     * 생성한 설문의 결과 조회
     * - 연령대 분포
     * - 성별 분포
     * - 객관식 분포
     * - 기타 응답(주관식) 포함
     * - 주관식 응답 리스트
     *
     * @param surveyId
     * @param userId
     * @return - 해당 설문이 존재하지 않으면 에러
     * - 요청자의 아이디가 설문 생성자의 아이디와 일치하지 않으면 에러
     */
    @Transactional(readOnly = true)
    public SurveyResultDTO getSurveyResponsesResult(Long surveyId, String userId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        //요청자 아이디와 설문 생성자 아이디 비교
        surveyService.validateUserMadeSurvey(userId, survey);

        //DB 조회 후 Mapper 객체로 매핑
        List<SurveyResultMapper> surveyResultMapperList = questionRepository.findQuestionsAndAnswersBySurveyId(surveyId)
                .stream().map(SurveyResultMapper::new).toList();

        //응답 객체 생성
        SurveyResultDTO surveyResultDTO = SurveyResultDTO.of(survey);

        //전체 응답자 수 추가
        long responseCount = respondRepository.countBySurvey_SurveyId(surveyId);
        surveyResultDTO.setResponseCount(responseCount);

        //응답 객체에 나이, 성별 분포 추가
        addAgeAndGenderDistribution(surveyId, surveyResultDTO);

        //질문 DB 쿼리 조회 결과를 질문 Map에 매핑(쿼리는 JOIN, GROUP BY 사용으로 한 질문 아이디에 대해 여러 행들이 값을 가질 수 있음)
        Map<Long, QuestionResultDTO> questionResultMap = new LinkedHashMap<>();

        //한 행에 대해(DB 쿼리 결과)
        for (SurveyResultMapper surveyResultMapper : surveyResultMapperList) {
            //질문 결과 DTO 초기화(질문 기본 정보 담기)
            initializeQuestionResultMap(surveyResultMapper, questionResultMap, surveyResultDTO);
            //해당 질문에 응답 추가
            addResponsesToQuestion(surveyResultMapper, questionResultMap.get(surveyResultMapper.getQuesId()));
        }
        return surveyResultDTO;
    }

    /**
     * 질문 결과 DTO 초기화 후 Map 저장
     * - 설문 결과를 담기 위한 질문 결과 DTO 생성 후 Map에 저장
     *
     * @param surveyResultMapper
     * @param questionMap
     * @param surveyResultDTO
     */
    private void initializeQuestionResultMap(SurveyResultMapper surveyResultMapper, Map<Long, QuestionResultDTO> questionMap, SurveyResultDTO surveyResultDTO) {
        Long quesId = surveyResultMapper.getQuesId();
        if (!questionMap.containsKey(quesId)) {
            //결과를 담기 위한 QuestionResultDTO 생성 후 초기화
            QuestionResultDTO questionResult = new QuestionResultDTO(surveyResultMapper);
            questionMap.put(quesId, questionResult);
            surveyResultDTO.getQuestionList().add(questionResult);
        }
    }

    /**
     * 질문 타입에 따라 응답 결과 저장
     * - 다중, 단일 객관식은 동일한 로직으로 처리
     * - 해당 보기에 대한 기본 정보 및 응답 수 저장
     * - 기타 질문일 경우 기타 응답 저장
     * - 주관식일 경우 주관식 응답 저장
     *
     * @param surveyResultMapper
     * @param question           - 만약 질문 타입이 3개중 해당 되지 않으면 유효하지 않은 질문 타입 에러 전송
     */
    private void addResponsesToQuestion(SurveyResultMapper surveyResultMapper, QuestionResultDTO question) {
        QuestionType questionType = surveyResultMapper.getQuestionType();
        switch (questionType) {
            case OBJ_MULTI:
            case OBJ_SINGLE:
                SelectionResultDTO selection = new SelectionResultDTO(surveyResultMapper);
                if (surveyResultMapper.getIsEtc() && surveyResultMapper.getEtcAnswer() != null) {
                    selection.getEtcAnswer().add(surveyResultMapper.getEtcAnswer());
                }
                question.getSelectionList().add(selection);
                break;
            case SUBJECTIVE:
                if (surveyResultMapper.getSubjectiveResponse() != null) {
                    question.getSubjAnswerList().add(surveyResultMapper.getSubjectiveResponse());
                }
                break;
            default:
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE);
        }
    }

    /**
     * 나이, 성별 분포 추가
     * - DB 조회 후 응답 DTO에 성별, 나이 분포 추가
     *
     * @param surveyId
     * @param surveyResultDTO
     */
    private void addAgeAndGenderDistribution(Long surveyId, SurveyResultDTO surveyResultDTO) {
        List<GenderCountDTO> genderCountDTOList = userService.getGenderCountListBySurveyId(surveyId);
        List<AgeCountDTO> ageCountDTOList = userService.getAgeGroupCountBySurveyId(surveyId);

        surveyResultDTO.setAgeCountList(ageCountDTOList);
        surveyResultDTO.setGenderCountList(genderCountDTOList);
    }


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
    public SurveyResponseDTO getUserRespondedSurvey(Long surveyId, String userId) {
        //요청 ID의 설문이 존재하지 않으면 에러 전송, 아닐 시 응답 DTO 객체 생성
        SurveyResponseDTO surveyResponseDTO = Optional.ofNullable(surveyRepository.getSurveyWithDetail(surveyId))
                .map(SurveyResponseDTO::of)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));

        //객관식 응답 Map 생성
        Map<Object, List<ObjAnswer>> objAnswerMap = getObjAnswerMap(surveyId, userId);
        //주관식 응답 Map 생성
        Map<Long, String> subjAnswerMap = getSubjAnswerMap(surveyId, userId);

        //질문에 생성한 주관식, 객관식 응답 할당
        surveyResponseDTO.getQuestionList()
                .forEach(question -> assignAnswersToQuestion(question, objAnswerMap, subjAnswerMap));

        return surveyResponseDTO;
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
    private void assignAnswersToQuestion(QuestionResponseDTO question, Map<Object, List<ObjAnswer>> objAnswerMap, Map<Long, String> subjAnswerMap) {
        if (!objAnswerMap.containsKey(question.getQuesId()) && !subjAnswerMap.containsKey(question.getQuesId())) {
            return;
        }
        switch (question.getQuestionType()) {
            case OBJ_MULTI:
                objAnswerMap.get(question.getQuesId()).forEach(objAnswer -> {
                    question.getObjAnswerList().add(objAnswer.getSelection().getId().getSequence());
                    setEtcAnswerIfPresent(question, objAnswer);
                });
                break;

            case OBJ_SINGLE:
                ObjAnswer objAnswer = objAnswerMap.get(question.getQuesId()).get(0);
                question.getObjAnswerList().add(objAnswer.getSelection().getId().getSequence());
                setEtcAnswerIfPresent(question, objAnswer);
                break;

            case SUBJECTIVE:
                question.setSubjAnswer(subjAnswerMap.get(question.getQuesId()));
                break;

            default:
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE);
        }
    }

    /**
     * 기타 응답이 존재할 시 추가하는 메서드
     *
     * @param question
     * @param objAnswer
     */
    private void setEtcAnswerIfPresent(QuestionResponseDTO question, ObjAnswer objAnswer) {
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
