package mcnc.survwey.api.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.global.utils.DecryptField;

@Data
@NoArgsConstructor
public class PasswordModifyDTO {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 5, max = 20, message = "사용자 아이디는 5글자 이상, 20글자 이하입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "사용자 아이디는 영문과 숫자의 조합이어야 합니다.")
    private String userId;

    @DecryptField
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+]).{8,100}$",
            message = "비밀번호는 최소 8자, 최대 100자, 숫자, 특수문자 및 대소문자를 포함해야 합니다."
    )
    private String password;
}
