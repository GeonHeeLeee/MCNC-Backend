package mcnc.survwey.api.survey.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.SurveyWithDetailDTO;
import mcnc.survwey.api.survey.service.SurveyModifyService;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.SurveyService;
import mcnc.survwey.domain.user.UserService;
import mcnc.survwey.global.config.SessionContext;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/survey")
public class SurveyModifyController {


    private final SurveyService surveyService;
    private final SurveyModifyService surveyModifyService;
    private final UserService userService;

    @PostMapping("/modify/{surveyId}")
    public ResponseEntity<Object> surveyModify (@Valid @RequestBody SurveyWithDetailDTO surveyWithDetailDTO){
        String userId = SessionContext.getCurrentUser();

        Survey survey = Optional.ofNullable(surveyModifyService.surveyModifyWithDetails(surveyWithDetailDTO, userId))
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
                //임시 에러 코드
        log.info(survey.toString());
        SurveyWithDetailDTO updatedSurvey = SurveyWithDetailDTO.of(survey);
        log.info(updatedSurvey.toString());
        return ResponseEntity.ok().body(updatedSurvey);
    }

}
