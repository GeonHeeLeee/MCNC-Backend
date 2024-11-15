package mcnc.survwey.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mcnc.survwey.domain.enums.Gender;

@Getter
@Setter
@AllArgsConstructor
public class GenderCountDTO {
    private Gender gender;
    private int count;
}
