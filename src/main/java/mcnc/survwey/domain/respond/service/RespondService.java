package mcnc.survwey.domain.respond.service;

import lombok.RequiredArgsConstructor;

import mcnc.survwey.domain.respond.repository.RespondRepository;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RespondService {
    private final RespondRepository respondRepository;

    /**
     * 해당 설문에 응답한 사용자가 있는지 검증
     * @param surveyId
     * - 응답한 사람이 존재하면 에러
     * @Author 이강민
     */
    public void existsBySurveyId(Long surveyId){
        if(respondRepository.existsBySurvey_SurveyId(surveyId)){
            throw new CustomException(HttpStatus.CONFLICT, ErrorCode.RESPOND_ALREADY_EXISTS);
            //응답한 사람이 존재하면 CONFLICT
        }
    }

    /**
     * 요청 사용자가 해당 설문에 이미 응답했는지 확인
     * - 응답 했으면 true
     * - 하지 않았으면 false
     * @param surveyId
     * @param userId
     * @return
     * @Author 이건희
     */
    public boolean isUserRespondedToSurvey(Long surveyId, String userId) {
        return respondRepository.existsBySurvey_SurveyIdAndUser_UserId(surveyId, userId);
    }
}
