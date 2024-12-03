package mcnc.survwey.api.survey.response.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenderCountDTO {
    private String gender;
    private long count;
}
