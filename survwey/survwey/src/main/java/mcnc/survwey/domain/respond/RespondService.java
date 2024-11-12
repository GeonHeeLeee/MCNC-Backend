package mcnc.survwey.domain.respond;

import lombok.RequiredArgsConstructor;
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
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE);
        }
    }
}
