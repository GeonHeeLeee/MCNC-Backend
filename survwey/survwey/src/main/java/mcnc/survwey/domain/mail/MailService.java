package mcnc.survwey.domain.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserService userService;
    private final SurveyService surveyService;

    @Value("${MAIL_USER_NAME}")
    private String senderEmail;

    public void sendLinkMessage(String userId, Long surveyId, String link){

        User user = userService.findByUserId(userId);
        Survey survey = surveyService.findBySurveyId(surveyId);

        surveyService.verifyUserMadeSurvey(userId, survey);
        //본인이 생성한 설문 확인

        try{

//            LocalDateTime surveyCreateDay = survey.getCreateDate();
            LocalDateTime surveyExpireDay = survey.getExpireDate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a h:mm");

            Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
            context.setVariable("inviterName", user.getName());
            context.setVariable("expireDate", surveyExpireDay.format(formatter));
            context.setVariable("surveyLink", link);

            String htmlContent = templateEngine.process("mail/send", context);//타임리프 템플릿 처리 후 HTML 콘텐츠 최종 생성

            MimeMessage message = mailSender.createMimeMessage();// 이메일 메시지 생성 객체
            MimeMessageHelper helper = new MimeMessageHelper(message, true);// T: html 형식, F: 텍스트 형식

            helper.setFrom(senderEmail);
            helper.setSubject(survey.getTitle());
            helper.setTo(user.getEmail());
            helper.setText(htmlContent, true);

            // 이미지 첨부 (첨부파일로 cid를 사용)
            ClassPathResource logoResource = new ClassPathResource("static/images/icon_logo.png");
            helper.addInline("logoImage", logoResource); // 이미지 ID 'logoImage'로 첨부

            ClassPathResource titleResource = new ClassPathResource("static/images/title.png");
            helper.addInline("titleImage", titleResource); // 이미지 ID 'titleImage'로 첨부

            mailSender.send(message);

        } catch (MailException | MessagingException e){
            throw new RuntimeException("메일 발송 실패 ", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
