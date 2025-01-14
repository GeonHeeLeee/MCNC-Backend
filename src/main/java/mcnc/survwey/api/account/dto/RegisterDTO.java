package mcnc.survwey.api.account.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.enums.Gender;
import mcnc.survwey.global.utils.DecryptField;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RegisterDTO {

    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 5, max = 20, message = "사용자 아이디는 5글자 이상, 20글자 이하입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$", message = "사용자 아이디는 5~20자의 영문과 숫자로 입력해주세요.")
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
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 날짜 형식 지정
    @Past(message = "생년월일은 과거 날짜여야 합니다.") // 과거 날짜만 허용
    private LocalDate birth;

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상, 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z가-힣 ]*$", message = "이름은 영어, 한글, 공백만 포함할 수 있습니다.")
    private String name;

    public User toEntity(String encryptedPassword) {
        return User.builder()
                .userId(this.getUserId())
                .email(this.getEmail())
                .password(encryptedPassword)
                .name(this.getName())
                .registerDate(LocalDateTime.now())
                .birth(this.getBirth())
                .gender(this.getGender())
                .build();
    }

}
