package mcnc.survwey.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailSendDTO {
    private String userId;
    private String email;
}
