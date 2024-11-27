package mcnc.survwey.domain.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.mail.utils.EncryptionUtil;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserRedisService;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.redis.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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
import java.util.UUID;

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
<<<<<<< HEAD
    public void mailSender(Context context, String title, String email) throws MessagingException {
=======
    public void sendMail(Context context, String title, String email, String link) throws MessagingException {
>>>>>>> 4a5b99b60dd77e8408302a9c88355f0dfc789fec
        String htmlContent = templateEngine.process("mail/send", context);//타임리프 템플릿 처리 후 HTML 콘텐츠 최종 생성
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
     * @param userId
     * @param surveyId
     */
<<<<<<< HEAD
    public void sendLinkMessage(String userId, Long surveyId){

        User user = userService.findByUserId(userId);
        Survey survey = surveyService.findBySurveyId(surveyId);

        //여기서 바로 호출하게 했음 Controller에서 호출하면 중복으로 Survey, User 등 조회하니까
        String encryptedLink = encryptLink(survey.getSurveyId());

        //유효성 검사
        surveyService.checkSurveyExpiration(survey.getExpireDate());//만료일 확인
=======
    public void sendLinkMessage(String userId, Long surveyId, String link){
        User user = userService.findByUserId(userId);
        Survey survey = surveyService.findBySurveyId(surveyId);
>>>>>>> 4a5b99b60dd77e8408302a9c88355f0dfc789fec
        surveyService.validateUserMadeSurvey(userId, survey);
        //본인이 생성한 설문 확인
        try{
            LocalDateTime surveyExpireDay = survey.getExpireDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a h:mm");
            //그거를 파라미터를 Map으로 받게 하면 되긴함 파라미터를 Map<String, Object> 이런식으로 받아서
            //임시 리팩토링 추후 생각
            Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
            context.setVariable("inviterName", user.getName());
            context.setVariable("surveyTitle", survey.getTitle());
            context.setVariable("surveyLink", encryptedLink);
            context.setVariable("expireDate", surveyExpireDay.format(formatter));

<<<<<<< HEAD
            log.info("surveyLink = {}", encryptedLink);//메일 전송 후 링크 클릭할 때 테스트 용 (지우지 마쎼용~)
            mailSender(context, survey.getTitle(), user.getEmail());
=======
            sendMail(context, survey.getTitle(), user.getEmail(), link);
>>>>>>> 4a5b99b60dd77e8408302a9c88355f0dfc789fec
        } catch (MailException | MessagingException e){
            throw new RuntimeException("메일 발송 실패 ", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * URL 파라미터 암호화
     * @param surveyId
     * @return
     */
    public String encryptLink(Long surveyId) {
        //EncryptLink에 중복 검사, Survey, User 중복 조회들 있어서 따로 뺐음
        String encryptedSurveyId = encryptionUtil.encrypt(surveyId.toString());
        return baseUrl + encryptedSurveyId;
    }

    /**
     * URL 파라미터 복호화
     * @param surveyId
     * @return
     */
    public String decryptLink(String surveyId){
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
        //어차피 Key에 들어가는건 생성자 아이디라 검증 안해도 됨
//        surveyService.validateUserMadeSurvey(userId, survey);
        //본인이 생성한 설문 확인
        try{
            Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
            context.setVariable("inviterName", user.getName());
            context.setVariable("surveyLink", link);
<<<<<<< HEAD
            mailSender(context, survey.getTitle(), user.getEmail());
=======
            sendMail(context, survey.getTitle(), user.getEmail(), link);
>>>>>>> 4a5b99b60dd77e8408302a9c88355f0dfc789fec
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
    public void sendPasswordModifyAuthNumber(String userId){
        User user = userService.findByUserId(userId);
        String tempAuthCode = KeyGenerators.string().generateKey().substring(0, 8);
        try{
            Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
            context.setVariable("inviterName", user.getName());
            context.setVariable("tempAuthCode", tempAuthCode);
            userRedisService.saveVerificationCode(userId, tempAuthCode);
            mailSender(context, "비밀번호 변경 인증번호 발급", user.getEmail());
        } catch (MailException | MessagingException e){
            throw new RuntimeException("메일 발송 실패 ", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
