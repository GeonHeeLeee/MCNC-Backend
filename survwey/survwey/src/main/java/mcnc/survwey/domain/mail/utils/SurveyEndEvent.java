package mcnc.survwey.domain.mail.utils;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SurveyEndEvent extends ApplicationEvent {
    private final Long surveyId;
    private final String surveyTitle;

    public SurveyEndEvent(Object source, Long surveyId, String surveyTitle) {
        super(source);
        this.surveyId = surveyId;
        this.surveyTitle = surveyTitle;
    }
}

