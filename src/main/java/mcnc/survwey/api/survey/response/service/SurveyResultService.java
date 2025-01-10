package mcnc.survwey.api.survey.response.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.response.dto.result.*;
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

import java.util.HashMap;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
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
     * @Author 이건희
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

        // 질문 및 응답 데이터 처리
        mapQuestionsAndResponses(surveyResultQueryDTOList, surveyResultDTO);

        return surveyResultDTO;
    }


    /**
     * 쿼리 결과에 따른 질문 처리 메서드
     * - 쿼리 결과에 중복된 질문들이 있으므로, 유일한 질문 초기화
     * - 초기화 된 질문에 답변 추가
     *
     * @param surveyResultQueryDTOList
     * @param surveyResultDTO
     * @Author 이건희
     */
    private void mapQuestionsAndResponses(List<SurveyResultQueryDTO> surveyResultQueryDTOList,
                                          SurveyResultDTO surveyResultDTO) {
        //중복되지 않은 QuestionResultDTO를 위한 Map(Key: quesId, Value: QuestionResultDTO)
        Map<Long, QuestionResultDTO> questionResultMap = new HashMap<>();

        //각각 쿼리 결과 행에 대해 처리
        for (SurveyResultQueryDTO queryDTO : surveyResultQueryDTOList) {
            Long quesId = queryDTO.getQuesId();

            //QuestionResultDTO 초기화: 없으면 computeIfAbsent로 저장 후 반환하고 있으면 가져오기
            QuestionResultDTO questionResult = questionResultMap.computeIfAbsent(quesId, k -> {
                QuestionResultDTO questionResultDTO = new QuestionResultDTO(queryDTO);
                surveyResultDTO.getQuestionList().add(questionResultDTO);
                return questionResultDTO;
            });

            //질문 타입에 따라 응답 처리
            assignResponsesByQuestionType(queryDTO, questionResult);
        }
    }


    /**
     * 질문 타입에 따른 응답 처리 메서드
     *
     * @param queryDTO
     * @param questionResult
     * @Author 이건희
     */
    private void assignResponsesByQuestionType(SurveyResultQueryDTO queryDTO, QuestionResultDTO questionResult) {
        switch (queryDTO.getQuestionType()) {
            //객관식은 다중, 단일 동일 처리
            case OBJ_MULTI:
            case OBJ_SINGLE:
                addObjectiveResponse(queryDTO, questionResult);
                break;
            //주관식 처리
            case SUBJECTIVE:
                addSubjectiveResponse(queryDTO, questionResult);
                break;
            default:
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE);
        }
    }

    /**
     * 객관식 응답 추가 메서드
     * - 각 질문에 맞는 객관식 응답 추가
     *
     * @param queryDTO
     * @param questionResult
     * @Author 이건희
     */
    private void addObjectiveResponse(SurveyResultQueryDTO queryDTO, QuestionResultDTO questionResult) {
        QuestionResultDTO.SelectionResultDTO existingSelection = questionResult.getSelectionList().stream()
                //순서(sequence)가 일치하는 보기 가져오기
                .filter(selection -> selection.getSequence() == queryDTO.getSequence())
                .findFirst()
                //해당하는 값이 없을 시 만들고 저장 후 가져오기
                .orElseGet(() -> {
                    QuestionResultDTO.SelectionResultDTO selectionResultDTO = new QuestionResultDTO.SelectionResultDTO(queryDTO);
                    questionResult.getSelectionList().add(selectionResultDTO);
                    return selectionResultDTO;
                });

        // 응답한 사람 수 업데이트
        existingSelection.setResponseCount((int) (existingSelection.getResponseCount() + queryDTO.getResponseCount()));

        //기타 응답이 있을 경우 업데이트
        if (queryDTO.getIsEtc() && queryDTO.getEtcAnswer() != null) {
            existingSelection.getEtcAnswer().add(queryDTO.getEtcAnswer());
        }
    }

    /**
     * 주관식 응답 추가 메서드
     *
     * @param queryDTO
     * @param questionResultDTO
     * @Author 이건희
     */
    private void addSubjectiveResponse(SurveyResultQueryDTO queryDTO, QuestionResultDTO questionResultDTO) {
        if (queryDTO.getSubjectiveResponse() != null) {
            questionResultDTO.getSubjAnswerList().add(queryDTO.getSubjectiveResponse());
        }
    }

    /**
     * 나이, 성별 분포 추가
     * - DB 조회 후 응답 DTO에 성별, 나이 분포 추가
     *
     * @param surveyId
     * @param surveyResultDTO
     * @Author 이건희
     */
    private void addAgeAndGenderDistribution(Long surveyId, SurveyResultDTO surveyResultDTO) {
        List<SurveyResultDTO.GenderCountDTO> genderCountDTOList = userService.getGenderCountListBySurveyId(surveyId);
        List<SurveyResultDTO.AgeCountDTO> ageCountDTOList = userService.getAgeGroupCountBySurveyId(surveyId);

        surveyResultDTO.setAgeCountList(ageCountDTOList);
        surveyResultDTO.setGenderCountList(genderCountDTOList);
    }


}
