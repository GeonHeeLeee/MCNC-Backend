package mcnc.survwey.api.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.enums.Gender;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private String userId;

    private String name;

    private String email;

    private LocalDate birth;

    private Gender gender;

    public static ProfileDTO of(User user, String encryptedEmail) {
        return ProfileDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(encryptedEmail)
                .birth(user.getBirth())
                .gender(user.getGender())
                .build();
    }
}
