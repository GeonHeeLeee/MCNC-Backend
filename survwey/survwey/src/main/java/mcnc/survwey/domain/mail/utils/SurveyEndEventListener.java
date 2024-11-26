package mcnc.survwey.domain.mail.utils;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.mail.service.MailService;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyEndEventListener {

    private final MailService mailService;
    private final SurveyService surveyService;

    @EventListener
    public void onSurveyEndEvent(SurveyEndEvent event){
        Survey survey = surveyService.findBySurveyId(event);
        mailService.sendVerifySurveyLink(event.getSurveyId(), );
    }

}
