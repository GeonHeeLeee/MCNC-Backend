package mcnc.survwey.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.LoginDTO;
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

    public void loginUser(LoginDTO loginDTO, HttpServletRequest request) {
        User foundUser = Optional.ofNullable(userService.findByUserId(loginDTO.getUserId()))
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(loginDTO.getPassword(), foundUser.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        createUserSession(loginDTO, request);
    }

    private void createUserSession(LoginDTO loginDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, loginDTO.getUserId());
    }
}
