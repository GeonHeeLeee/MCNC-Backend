package mcnc.survwey.domain.survey.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.manage.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.repository.SurveyRepository;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SurveyService {
    private final SurveyRepository surveyRepository;

    /**
     * 설문 생성, 저장 메서드
     * @param surveyWithDetailDTO
     * @param creator
     * @return
     * @Author 이건희
     */
    public Survey buildAndSaveSurvey(SurveyWithDetailDTO surveyWithDetailDTO, User creator) {
        Survey createdSurvey = surveyWithDetailDTO.toEntity(surveyWithDetailDTO.getSurveyId(), creator);
        surveyRepository.save(createdSurvey);
        return createdSurvey;
    }


    /**
     * 설문 아이디로 설문 조회
     * @param surveyId
     * @return
     * - 해당하는 설문이 존재하지 않을 시 에러
     * @Author 이강민
     */
    public Survey findBySurveyId(Long surveyId) {
        return surveyRepository.findById(surveyId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
    }

    /**
     * 설문 만료 확인
     * - 설문 만료일이 현재 시간보다 이전이면 에러
     * @param expireDate
     * @Author 이건희
     */
    public void checkSurveyExpiration(LocalDateTime expireDate) {
        if (expireDate.isBefore(LocalDateTime.now())
                || expireDate.isEqual(LocalDateTime.now())) {
            throw new CustomException(HttpStatus.GONE, ErrorCode.EXPIRED_SURVEY);
        }
    }

    /**
     * 해당 설문을 요청자가 만든 것인지 확인
     * - 생성자와 요청자가 일치하지 않으면 에러
     * @param userId
     * @param survey
     * @Author 이건희
     */
    public void validateUserMadeSurvey(String userId, Survey survey) {
        if (!survey.getUser().getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, ErrorCode.SURVEY_CREATOR_NOT_MATCH);
        }
    }


}
