package mcnc.survwey.api.mail.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.user.User;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThymeleafUtil {
    /**
     * 날짜 형식에 맞게 변환
     *
     * @param dateTime
     * @return
     */
    public String getFormatedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a h:mm");
        return dateTime.format(formatter);
    }

    /**
     * 설문 초대 thymeleaf 템플릿에 전달할 데이터 저장
     * @param surveyToInvite
     * @param sender
     * @param encryptedLink
     * @return
     */
    public Context initInvitationContext(Survey surveyToInvite, User sender, String encryptedLink){
        Context context = new Context();//타임리프 템플릿에 전달할 데이터를 저장하는 컨테이너
        context.setVariable("inviterName", sender.getName());
        context.setVariable("surveyTitle", surveyToInvite.getTitle());
        context.setVariable("surveyLink", encryptedLink);
        context.setVariable("expireDate", getFormatedDate(surveyToInvite.getExpireDate()));

        return context;
    }

    /**
     * 설문 결과 알림 thymeleaf 템플릿에 전달할 데이터 저장
     * @param user
     * @param encryptedLink
     * @return
     */
    public Context initNotificationContext(User user, String title, String encryptedLink){
        Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
        context.setVariable("inviterName", user.getName());
        context.setVariable("surveyTitle", title);
        context.setVariable("surveyLink", encryptedLink);
        return context;
    }

    /**
     * 비밀번호 찾기 인증 thymeleaf 템플릿에 전달할 데이터 저장
     * @param user
     * @param tempAuthCode
     * @return
     */
    public Context initAutheticationContext(User user, String tempAuthCode){
        Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
        context.setVariable("receiverName", user.getName());
        context.setVariable("tempAuthCode", tempAuthCode);
        context.setVariable("expireDate", getFormatedDate(LocalDateTime.now().plusMinutes(10)));
        return context;
    }

}
