package mcnc.survwey.domain.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileModifyDTO {

    private String name;
    private String email;
}
