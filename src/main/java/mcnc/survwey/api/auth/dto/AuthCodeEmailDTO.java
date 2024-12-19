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
public class AuthCodeEmailDTO {

    @DecryptField
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "임시 인증번호는 필수입니다.")
    private String tempAuthCode;

}
