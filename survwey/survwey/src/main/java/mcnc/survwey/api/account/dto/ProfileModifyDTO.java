package mcnc.survwey.api.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileModifyDTO {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상, 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z가-힣 ]*$", message = "이름은 영어, 한글, 공백만 포함할 수 있습니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;
}
