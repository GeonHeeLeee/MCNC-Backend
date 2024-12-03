package mcnc.survwey.api.survey.response.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AgeCountDTO {
    private String age;
    private long count;
}
