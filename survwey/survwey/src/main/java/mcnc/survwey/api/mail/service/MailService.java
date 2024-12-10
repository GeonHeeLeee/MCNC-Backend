package mcnc.survwey.api.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.utils.EncryptionUtil;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.service.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserRedisService;
import mcnc.survwey.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mcnc.survwey.global.exception.custom.ErrorCode.*;

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
    public void sendMail(Context context, String title, String email, String htmlPath) {
        String htmlContent = templateEngine.process(htmlPath, context);//타임리프 템플릿 처리 후 HTML 콘텐츠 최종 생성
        MimeMessage message = mailSender.createMimeMessage();// 이메일 메시지 생성 객체
        try {
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
        } catch (MessagingException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, FAILED_TO_SEND_EMAIL);
        }
    }


    /**
     * 설문 초대
     *
     * @param senderId
     * @param surveyId
     * @param encryptedEmailList
     * @throws MessagingException
     */
    public void sendInvitationLink(String senderId, Long surveyId, List<String> encryptedEmailList) {
        User sender = userService.findByUserId(senderId);
        Survey surveyToInvite = surveyService.findBySurveyId(surveyId);
        String encryptedLink = encryptLink(surveyToInvite.getSurveyId());

        //유효성 검사
        surveyService.checkSurveyExpiration(surveyToInvite.getExpireDate());//만료일 확인
        surveyService.validateUserMadeSurvey(senderId, surveyToInvite);//본인이 생성한 설문 확인

        //암호화 된 이메일 복호화
        List<String> decryptedEmailList = encryptionUtil.decryptList(encryptedEmailList);
        //이메일 요청 정규식 검사
        validateEmailRequest(decryptedEmailList);

        //외부 선언 시 병렬 스트림에서 타임리프를 못 읽는 문제가 발생하여 독립적인 Context 생성
        decryptedEmailList.parallelStream()
                .forEach(recipientEmail -> {
                    Context context = new Context(); //타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
                    context.setVariable("inviterName", sender.getName());
                    context.setVariable("surveyTitle", surveyToInvite.getTitle());
                    context.setVariable("surveyLink", encryptedLink);
                    context.setVariable("expireDate", getFormatedDate(surveyToInvite.getExpireDate()));

                    sendMail(context, surveyToInvite.getTitle(), recipientEmail, "mail/invitation");
                });
    }

    /**
     * 이메일 요청 정규식 검사
     * - 이메일 형식에 맞지 않을 경우 400 에러 응답
     *
     * @param emailList
     */
    public void validateEmailRequest(List<String> emailList) {
        String emailPattern = "^(?=.{1,255}$)(?![_.-])[A-Za-z0-9._-]+(?<![_.-])@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        for (String email : emailList) {
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                throw new CustomException(HttpStatus.BAD_REQUEST, INVALID_EMAIL_FORMAT);
            }
        }
    }

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
        Context context = new Context();//타임리프 템플릿에 전달할 데이터 저장하는 컨테이너
        context.setVariable("inviterName", user.getName());
        context.setVariable("surveyLink", link);

        sendMail(context, survey.getTitle(), user.getEmail(), "mail/notification");

    }

    /**
     * 비밀번호 찾기 인증 메일
     *
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
