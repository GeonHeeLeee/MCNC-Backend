package mcnc.survwey.domain.respond;

import lombok.RequiredArgsConstructor;

import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RespondService {
    private final RespondRepository respondRepository;

    public Respond respondExist(Long id){

        return respondRepository.findBySurvey_SurveyId(id)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE));
        //임시 에러 코드
    }
}
