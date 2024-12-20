package mcnc.survwey.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.global.utils.DecryptField;

@Data
@NoArgsConstructor
public class PasswordAuthDTO {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 5, max = 20, message = "사용자 아이디는 5글자 이상, 20글자 이하입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$", message = "사용자 아이디는 5~20자의 영문과 숫자로 입력해주세요.")
    private String userId;

    @DecryptField
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;
}
