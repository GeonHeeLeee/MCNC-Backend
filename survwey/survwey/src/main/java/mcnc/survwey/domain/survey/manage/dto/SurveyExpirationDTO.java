package mcnc.survwey.domain.survey.manage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyExpirationDTO {

    private Long surveyId;
    private LocalDateTime expiredDate;
    private boolean forceClose; // 설문 강제 종료 버튼 여부: true-강제 종료

}
