package mcnc.survwey.api.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.mail.utils.ThymeleafUtil;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.service.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserRedisService;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mcnc.survwey.global.exception.custom.ErrorCode.FAILED_TO_SEND_EMAIL;
import static mcnc.survwey.global.exception.custom.ErrorCode.INVALID_EMAIL_FORMAT;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final UserService userService;
    private final SurveyService surveyService;
    private final EncryptionUtil encryptionUtil;
    private final UserRedisService userRedisService;
    private final ThymeleafUtil thymeleafUtil;

    @Value("${MAIL_USER_NAME}")
    private String senderEmail;

    @Value("${INVITATION_URL}")
    private String invitationUrl;

    @Value("${NOTIFICATION_URL}")
    private String notificationUrl;

    private static final String LOGO_IMAGE_PATH = "static/images/icon_logo.png";
    private static final String TITLE_IMAGE_PATH = "static/images/title.png";

    private static final Map<String, ClassPathResource> imageCache = new ConcurrentHashMap<>();

    private ClassPathResource getImage(String imagePath) {
        return imageCache.computeIfAbsent(imagePath, ClassPathResource::new);
    }

    /**
     * Thymeleaf 템플릿에 저장된 데이터, 제목, 이메일, 경로를 받아서 메일 전송
     *
     * @param context
     * @param title
     * @param email
     * @param htmlPath
     */
    public void sendMail(Context context, String title, String email, String htmlPath) {
        String htmlContent = templateEngine.process(htmlPath, context);//타임리프 템플릿 처리 후 HTML 콘텐츠 최종 생성
        MimeMessage message = mailSender.createMimeMessage();// 이메일 메시지 생성 객체
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);//파일첨부 혀용

            //메일 보내는 email
            helper.setFrom(senderEmail);
            //메일 제목
            helper.setSubject(title);
            //메일 받는 email
            helper.setTo(email);
            //메일에 보낼 html
            helper.setText(htmlContent, true);

            // 이미지 첨부 (첨부파일로 cid를 사용)
            helper.addInline("logoImage", getImage(LOGO_IMAGE_PATH));
            helper.addInline("titleImage", getImage(TITLE_IMAGE_PATH));

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
        //암호화 된 이메일 복호화
        List<String> decryptedEmailList = encryptionUtil.decryptList(encryptedEmailList);
        //이메일 요청 정규식 검사
        validateEmailRequest(decryptedEmailList);

        User sender = userService.findByUserId(senderId);
        Survey surveyToInvite = surveyService.findBySurveyId(surveyId);

        //유효성 검사
        surveyService.validateUserMadeSurvey(senderId, surveyToInvite);//본인이 생성한 설문 확인
        surveyService.checkSurveyExpiration(surveyToInvite.getExpireDate());//만료일 확인

        String encryptedLink = encryptionUtil.encryptURL(invitationUrl, surveyToInvite.getSurveyId());

        //외부 선언 시 병렬 스트림에서 타임리프를 못 읽는 문제가 발생하여 독립적인 Context 생성
        decryptedEmailList.parallelStream()
                .forEach(recipientEmail -> {
                    Context context = thymeleafUtil.initInvitationContext(surveyToInvite, sender, encryptedLink);
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
        //유효한 이메일인지 검증
        String emailPattern = "^(?=.{1,255}$)(?![_.-])[A-Za-z0-9._-]+(?<![_.-])@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        for (String email : emailList) {
            Matcher matcher = pattern.matcher(email);
            //유효한 이메일이 아닐 경우 에러 코드
            if (!matcher.matches()) {
                throw new CustomException(HttpStatus.BAD_REQUEST, INVALID_EMAIL_FORMAT);
            }
        }
    }

    /**
     * 설문결과 알림
     *
     * @param userId
     * @param surveyId
     */
    public void sendExpiredNotificationLink(String userId, Long surveyId) {
        User user = userService.findByUserId(userId);
        Survey survey = surveyService.findBySurveyId(surveyId);
        String encryptedUrl = encryptionUtil.encryptURL(notificationUrl, survey.getSurveyId());
        Context context = thymeleafUtil.initNotificationContext(user, survey, encryptedUrl);
        sendMail(context, survey.getTitle(), user.getEmail(), "mail/notification");
    }

    /**
     * 비밀번호 찾기 인증 메일 전송
     *
     * @param user
     */
    public void sendPasswordModifyAuthCode(User user) {
        //인증번호 생성
        String tempAuthCode = KeyGenerators.string().generateKey().substring(0, 8);
        Context context = thymeleafUtil.initPasswordAuthContext(user, tempAuthCode);
        //redis에 인증번호 저장
        userRedisService.saveVerificationCode(user.getUserId(), tempAuthCode);
        sendMail(context, "Survwey 비밀번호 변경 인증번호 발급", user.getEmail(), "mail/passwordAuth");
    }

    /**
     * 이메일 인증 메일 전송
     *
     * @param email
     * @throws Exception
     */
    public void sendEmailVerifyAuthCode(String email) {
        //인증번호 생성
        String tempAuthCode = KeyGenerators.string().generateKey().substring(0, 8);
        Context context = thymeleafUtil.initEmailVerifyContext(tempAuthCode);
        //redis에 인증번호 저장
        userRedisService.saveVerificationCode(email, tempAuthCode);
        sendMail(context, "Survwey 회원가입 이메일 인증", email, "mail/emailAuth");
    }
}
