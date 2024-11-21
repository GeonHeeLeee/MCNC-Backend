package mcnc.survwey.domain.mail;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailDTO {
    private String emailRecipient;
    private String emailTitle;
    private String emailContent;
    //설문 아이디(조회용)
    //받는 사람 이메일--
}
