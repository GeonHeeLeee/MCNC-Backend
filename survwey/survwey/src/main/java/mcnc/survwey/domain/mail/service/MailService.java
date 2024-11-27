package mcnc.survwey.domain.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.mail.utils.EncryptionUtil;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;


@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserService userService;
    private final SurveyService surveyService;
    private final EncryptionUtil encryptionUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${MAIL_USER_NAME}")
    private String senderEmail;
    @Value("${BASE_URL}")
    private String baseUrl;

    /**
     * 설문 초대
     * @param userId
     * @param surveyId
     * @param link
     */
    public void sendLinkMessage(String userId, Long surveyId, String link){

        User user = userService.findByUserId(userId);
        Survey survey = surveyService.findBySurveyId(surveyId);

        surveyService.validateUserMadeSurvey(userId, survey);
        //본인이 생성한 설문 확인

        try{
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

            log.info("link = {}", link);
            mailSender.send(message);

        } catch (MailException | MessagingException e){
            throw new RuntimeException("메일 발송 실패 ", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * URL 파라미터 암호화
     * @param surveyId
     * @param userId
     * @return
     */
    public String encryptedLink(Long surveyId, String userId) {
        Survey survey = surveyService.findBySurveyId(surveyId);//설문 존재 하는지 확인
        surveyService.validateUserMadeSurvey(userId, survey);//사용자 본인이 만들었는지 확인
        surveyService.checkSurveyExpiration(survey.getExpireDate());//만료일 확인

        String encryptedSurveyId = encryptionUtil.encrypt(surveyId.toString());
        return baseUrl + encryptedSurveyId;
    }

    /**
     * URL 파라미터 복호화
     * @param surveyId
     * @return
     */
    public String decryptedLink(String surveyId){
        Survey survey = surveyService.findBySurveyId(Long.parseLong(surveyId));//설문이 존재하는지 확인
        surveyService.checkSurveyExpiration(survey.getExpireDate());//만료일 확인

        return baseUrl + surveyId;
    }

    /**
     * 설문결과 알림
     * @param userId
     * @param surveyId
     * @param link
     */
    public void sendVerifySurveyLink(String userId, Long surveyId, String link){

        User user = userService.findByUserId(userId);
        Survey survey = surveyService.findBySurveyId(surveyId);

        surveyService.validateUserMadeSurvey(userId, survey);
        //본인이 생성한 설문 확인

        try{
            Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
            context.setVariable("inviterName", user.getName());
            context.setVariable("surveyLink", link);

            String htmlContent = templateEngine.process("mail/notification", context);//타임리프 템플릿 처리 후 HTML 콘텐츠 최종 생성

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

            log.info("link = {}", link);
            mailSender.send(message);

        } catch (MailException | MessagingException e){
            throw new RuntimeException("메일 발송 실패 ", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 비밀번호 찾기 인증 메일
     * @param userId
     */
    public void sendPasswordModifyLink(String userId){
        log.info("userId = {}", userId);
        User user = userService.findByUserId(userId);
        String password = createCode();
        try{
            Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
            context.setVariable("inviterName", user.getName());
            context.setVariable("temporaryPassword", password);

            String htmlContent = templateEngine.process("mail/authentication", context);//타임리프 템플릿 처리 후 HTML 콘텐츠 최종 생성

            MimeMessage message = mailSender.createMimeMessage();// 이메일 메시지 생성 객체
            MimeMessageHelper helper = new MimeMessageHelper(message, true);// T: html 형식, F: 텍스트 형식

            helper.setFrom(senderEmail);
            helper.setSubject("[Survwey] 인증번호 발급");
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

    //인증 번호
    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0: key.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1: key.append((char) ((int) random.nextInt(26) + 65)); break;
                default: key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }
}
