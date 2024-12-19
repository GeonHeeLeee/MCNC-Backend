package mcnc.survwey.api.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.mail.service.MailService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.api.auth.dto.PasswordAuthDTO;
import mcnc.survwey.api.auth.dto.LoginDTO;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static mcnc.survwey.global.config.AuthInterceptor.LOGIN_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    /**
     * 사용자 로그인
     * - 로그인 성공 시 세션 발급
     * @param loginDTO
     * @param request
     * - 해당 사용자가 존재하지 않으면 에러
     * - 비밀번호가 일치하지 않으면 에러
     */
    public void loginUser(LoginDTO loginDTO, HttpServletRequest request) {
        //사용자 id를 찾아서 존재하지 않으면 에러 코드
        User foundUser = userService.findByUserId(loginDTO.getUserId());

        //비밀번호가 일치하지 않을 경우 에러 코드
        if (!passwordEncoder.matches(loginDTO.getPassword(), foundUser.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        createUserSession(loginDTO, request);
    }

    /**
     * 세션 생성 메서드
     * @param loginDTO
     * @param request
     */
    private void createUserSession(LoginDTO loginDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, loginDTO.getUserId());
    }

    /**
     * 이메일이 일치하는지 확인, 전송
     * - 요청한 이메일이 일치하는지 확인
     * - 일치하다면 해당 이메일로 전송
     * @param passwordAuthDTO
     * @return
     */
    public void sendPasswordAuthCodeAfterValidation(PasswordAuthDTO passwordAuthDTO) {
        User user = userService.findByUserId(passwordAuthDTO.getUserId());
        //이메일가 일치하는지 확인 있으면 true, 없으면 false
        if(!user.getEmail().equals(passwordAuthDTO.getEmail())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_DOES_NOT_MATCH);
        }
        mailService.sendPasswordModifyAuthCode(user);
    }

    /**
     * 이메일 중복 확인 후, 중복이 되지 않으면 이메일 인증 번호 전송
     * - 이메일이 중복되지 않는지 확인
     * - 중복되지 않으면 해당 이메일로 인증 번호 전송
     * @param email
     */
    public void sendEmailAuthCodeAfterValidation(String email) {
        if(userService.isEmailDuplicated(email)) {
            throw new CustomException(HttpStatus.CONFLICT, ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }
        mailService.sendEmailVerifyAuthCode(email);
    }
}
