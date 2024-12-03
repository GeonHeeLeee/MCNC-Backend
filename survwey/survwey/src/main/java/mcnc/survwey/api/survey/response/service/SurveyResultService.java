package mcnc.survwey.api.survey.response.service;


import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.response.dto.result.*;
import mcnc.survwey.domain.question.enums.QuestionType;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.respond.repository.RespondRepository;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.service.SurveyService;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class SurveyResultService {

    private final UserService userService;
    private final SurveyService surveyService;
    private final RespondRepository respondRepository;
    private final QuestionRepository questionRepository;

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
        List<SurveyResultQueryDTO> surveyResultQueryDTOList = questionRepository.findQuestionsAndAnswersBySurveyId(surveyId)
                .stream().map(SurveyResultQueryDTO::new).toList();

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
        for (SurveyResultQueryDTO surveyResultQueryDTO : surveyResultQueryDTOList) {
            //질문 결과 DTO 초기화(질문 기본 정보 담기)
            initializeQuestionResultMap(surveyResultQueryDTO, questionResultMap, surveyResultDTO);
            //해당 질문에 응답 추가
            addResponsesToQuestion(surveyResultQueryDTO, questionResultMap.get(surveyResultQueryDTO.getQuesId()));
        }
        return surveyResultDTO;
    }



    /**
     * 질문 결과 DTO 초기화 후 Map 저장
     * - 설문 결과를 담기 위한 질문 결과 DTO 생성 후 Map에 저장
     *
     * @param surveyResultQueryDTO
     * @param questionMap
     * @param surveyResultDTO
     */
    private void initializeQuestionResultMap(SurveyResultQueryDTO surveyResultQueryDTO, Map<Long, QuestionResultDTO> questionMap, SurveyResultDTO surveyResultDTO) {
        Long quesId = surveyResultQueryDTO.getQuesId();
        if (!questionMap.containsKey(quesId)) {
            //결과를 담기 위한 QuestionResultDTO 생성 후 초기화
            QuestionResultDTO questionResult = new QuestionResultDTO(surveyResultQueryDTO);
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
     * @param surveyResultQueryDTO
     * @param question           - 만약 질문 타입이 3개중 해당 되지 않으면 유효하지 않은 질문 타입 에러 전송
     */
    private void addResponsesToQuestion(SurveyResultQueryDTO surveyResultQueryDTO, QuestionResultDTO question) {
        QuestionType questionType = surveyResultQueryDTO.getQuestionType();
        switch (questionType) {
            case OBJ_MULTI:
            case OBJ_SINGLE:
                SelectionResultDTO selection = new SelectionResultDTO(surveyResultQueryDTO);
                if (surveyResultQueryDTO.getIsEtc() && surveyResultQueryDTO.getEtcAnswer() != null) {
                    selection.getEtcAnswer().add(surveyResultQueryDTO.getEtcAnswer());
                }
                question.getSelectionList().add(selection);
                break;
            case SUBJECTIVE:
                if (surveyResultQueryDTO.getSubjectiveResponse() != null) {
                    question.getSubjAnswerList().add(surveyResultQueryDTO.getSubjectiveResponse());
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


}
