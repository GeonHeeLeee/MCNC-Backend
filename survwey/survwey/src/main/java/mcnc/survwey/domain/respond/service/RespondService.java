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

    public void existsBySurveyId(Long surveyId){
        if(respondRepository.existsBySurvey_SurveyId(surveyId)){
            throw new CustomException(HttpStatus.CONFLICT, ErrorCode.RESPOND_ALREADY_EXISTS);
            //응답한 사람이 존재하면 CONFLICT
        }
    }
    public void validateUserResponseToSurvey(Long surveyId, String userId) {
        if(!respondRepository.existsBySurvey_SurveyIdAndUser_UserId(surveyId, userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.HAS_NOT_RESPOND_TO_SURVEY);
        }
    }
}
