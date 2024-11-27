package mcnc.survwey.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.mail.service.MailService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.LoginDTO;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        User foundUser = Optional.ofNullable(userService.findByUserId(loginDTO.getUserId()))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_BY_ID));

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
}
