package mcnc.survwey.api.survey.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.sql.Timestamp;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyWithCountDTO extends SurveyDTO {
    private long respondCount;

    public static SurveyWithCountDTO of(Object[] record) {
        return SurveyWithCountDTO.builder()
                .surveyId((Long) record[0])
                .title((String) record[1])
                .description((String) record[2])
                .createDate(((Timestamp) record[3]).toLocalDateTime())
                .expireDate(((Timestamp) record[4]).toLocalDateTime())
                .respondCount(((Number) record[5]).intValue())
                .build();
    }

}
