package mcnc.survwey.api.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.global.utils.EncryptionUtil;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.service.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserRedisService;
import mcnc.survwey.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserService userService;
    private final SurveyService surveyService;
    private final EncryptionUtil encryptionUtil;
    private final UserRedisService userRedisService;

    @Value("${MAIL_USER_NAME}")
    private String senderEmail;

    @Value("${BASE_URL}")
    private String baseUrl;

    @Value("${SURVEY_VERIFY_URL}")
    private String notificationUrl;

    private static final String LOGO_IMAGE_PATH = "static/images/icon_logo.png";
    private static final String TITLE_IMAGE_PATH = "static/images/title.png";

    //메일 보내는 메소드
    public void sendMail(Context context, String title, String email, String htmlPath) throws MessagingException {
        String htmlContent = templateEngine.process(htmlPath, context);//타임리프 템플릿 처리 후 HTML 콘텐츠 최종 생성
        MimeMessage message = mailSender.createMimeMessage();// 이메일 메시지 생성 객체
        MimeMessageHelper helper = new MimeMessageHelper(message, true);// T: html 형식, F: 텍스트 형식

        helper.setFrom(senderEmail);
        helper.setSubject(title);
        helper.setTo(email);
        helper.setText(htmlContent, true);

        // 이미지 첨부 (첨부파일로 cid를 사용)
        ClassPathResource logoResource = new ClassPathResource(LOGO_IMAGE_PATH);
        helper.addInline("logoImage", logoResource); // 이미지 ID 'logoImage'로 첨부
        ClassPathResource titleResource = new ClassPathResource(TITLE_IMAGE_PATH);
        helper.addInline("titleImage", titleResource); // 이미지 ID 'titleImage'로 첨부

        mailSender.send(message);
    }


    /**
     * 설문 초대
     * @param senderId
     * @param surveyId
     * @param recipients
     * @throws MessagingException
     */
    public void sendInvitationLink(String senderId, Long surveyId, List<String> recipients) throws MessagingException {
        User sender = userService.findByUserId(senderId);
        Survey surveyToInvite = surveyService.findBySurveyId(surveyId);
        String encryptedLink = encryptLink(surveyToInvite.getSurveyId());

        //유효성 검사
        surveyService.checkSurveyExpiration(surveyToInvite.getExpireDate());//만료일 확인
        surveyService.validateUserMadeSurvey(senderId, surveyToInvite);//본인이 생성한 설문 확인

        Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
        context.setVariable("inviterName", sender.getName());
        context.setVariable("surveyTitle", surveyToInvite.getTitle());
        context.setVariable("surveyLink", encryptedLink);
        context.setVariable("expireDate", getFormatedDate(surveyToInvite.getExpireDate()));

        recipients.parallelStream()
                .forEach(recipientEmail -> {
                    try {
                        sendMail(context, surveyToInvite.getTitle(), recipientEmail, "mail/invitation");
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public String getFormatedDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a h:mm");
        return dateTime.format(formatter);
    }

    /**
     * URL 파라미터 암호화
     *
     * @param surveyId
     * @return
     */
    public String encryptLink(Long surveyId) {
        //EncryptLink에 중복 검사, Survey, User 중복 조회들 있어서 따로 뺐음
        String encryptedSurveyId = encryptionUtil.encrypt(surveyId.toString());
        return baseUrl + encryptedSurveyId;
    }

    /**
     * 설문결과 알림
     *
     * @param userId
     * @param surveyId
     * @param link
     */
    public void sendVerifySurveyLink(String userId, Long surveyId, String link) {
        User user = userService.findByUserId(userId);
        Survey survey = surveyService.findBySurveyId(surveyId);
        try {
            Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
            context.setVariable("inviterName", user.getName());
            context.setVariable("surveyLink", link);

            sendMail(context, survey.getTitle(), user.getEmail(), "mail/notification");
        } catch (MailException | MessagingException e) {
            throw new RuntimeException("메일 발송 실패 ", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 비밀번호 찾기 인증 메일
     * @param user
     * @throws Exception
     */
    public void sendPasswordModifyAuthCode(User user) throws Exception {
        String tempAuthCode = KeyGenerators.string().generateKey().substring(0, 8);

        Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
        context.setVariable("receiverName", user.getName());
        context.setVariable("tempAuthCode", tempAuthCode);
        context.setVariable("expireDate", getFormatedDate(LocalDateTime.now().plusMinutes(10)));
        userRedisService.saveVerificationCode(user.getUserId(), tempAuthCode);
        sendMail(context, "Survwey 비밀번호 변경 인증번호 발급", user.getEmail(), "mail/authentication");
    }

}
