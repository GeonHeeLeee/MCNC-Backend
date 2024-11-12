package mcnc.survwey.domain.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.enums.Gender;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ModifyDTO {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 5, max = 20, message = "사용자 아이디는 5글자 이상, 20글자 이하입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "사용자 아이디는 영문과 숫자의 조합이어야 합니다.")
    private String userId;

    @NotNull(message = "생년월일은 필수입니다.")
//    @Pattern(
//            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
//            message = "생년월일은 yyyy-MM-dd 형식이어야 합니다.")front랑 맞추자
    private LocalDate birth;

    @NotNull(message = "성별은 필수입니다.")
//    @Pattern(
//            regexp = "^[MF]$\n",
//            message = "성별은 M, F만 포함해야 합니다."
//    )
    private Gender gender;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;
}
