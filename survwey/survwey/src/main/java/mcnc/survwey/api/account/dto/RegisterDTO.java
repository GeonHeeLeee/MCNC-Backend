package mcnc.survwey.api.account.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.user.enums.Gender;
import mcnc.survwey.global.utils.DecryptField;

import java.time.LocalDate;

@Data
@Builder
public class RegisterDTO {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 5, max = 20, message = "사용자 아이디는 5글자 이상, 20글자 이하입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "사용자 아이디는 영문과 숫자의 조합이어야 합니다.")
    private String userId;

    @DecryptField
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @DecryptField
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+]).{8,100}$",
            message = "비밀번호는 최소 8자, 최대 100자, 숫자, 특수문자 및 대소문자를 포함해야 합니다."
    )
    private String password;

    @NotNull(message = "생년월일은 필수입니다.")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "올바르지 않은 생년월일 형식입니다."
    )
    private LocalDate birth;

    @NotNull(message = "성별은 필수입니다.")
    @Pattern(
            regexp = "^[MF]$",
            message = "올바르지 않은 성별입니다."
    )
    private Gender gender;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상, 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z가-힣 ]*$", message = "이름은 영어, 한글, 공백만 포함할 수 있습니다.")
    private String name;

}
