package mcnc.survwey.domain.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.enums.Gender;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ModifyDTO {

    private LocalDate birth;

    private Gender gender;

    private String name;
}
