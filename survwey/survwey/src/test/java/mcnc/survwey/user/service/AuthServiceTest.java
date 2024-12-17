package mcnc.survwey.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mcnc.survwey.api.account.dto.RegisterDTO;
import mcnc.survwey.api.account.service.AccountService;
import mcnc.survwey.api.auth.dto.AuthCodeDTO;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.api.auth.dto.LoginDTO;
import mcnc.survwey.api.auth.service.AuthService;
import mcnc.survwey.domain.user.enums.Gender;
import mcnc.survwey.domain.user.service.UserRedisService;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;

import static mcnc.survwey.global.config.AuthInterceptor.LOGIN_USER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRedisService userRedisService;

    private User userDTO;

    @BeforeEach
    public void saveUser(){
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUserId("asd123");
        registerDTO.setEmail("ccJ2ky8W1P-xITy45CREINOVq91sKDTABKPUnVjiwDc");
        registerDTO.setName("tester");
        registerDTO.setPassword("-b4tdMhq4MAQpTFwIRLVRg");
        registerDTO.setBirth(LocalDate.now());
        registerDTO.setGender(Gender.F);

        accountService.registerUser(registerDTO);

        userDTO = userService.findByUserId("asd123");

    }

    @Test
    void 로그인_성공_테스트() {
        // given
        LoginDTO loginDTO = LoginDTO.builder()
                .userId("asd123")
                .password("-b4tdMhq4MAQpTFwIRLVRg")
                .build();

        // when & then
        assertThatCode(() -> authService.loginUser(loginDTO, request))
                .doesNotThrowAnyException();
    }

    @Test
    void 존재하지_않는_사용자_로그인_시도() {
        // given
        LoginDTO loginDTO = LoginDTO.builder()
                .userId("asd1234")
                .password("-b4tdMhq4MAQpTFwIRLVRg")
                .build();

        // when & then
        assertThatCode(() -> authService.loginUser(loginDTO, request))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("해당 아이디의 사용자가 존재하지 않습니다.");
    }

    @Test
    void 잘못된_비밀번호_로그인_시도() {
        // given
        LoginDTO loginDTO = LoginDTO.builder()
                .userId("asd123")
                .password("qwer1234!!!!!!!!!!")
                .build();

        // when & then
        assertThatCode(() -> authService.loginUser(loginDTO, request))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }

    @Test
    void 비밀번호_변경_인증번호_체크(){
        //given
        AuthCodeDTO authCodeDTO = new AuthCodeDTO();
        authCodeDTO.setUserId("asd123");
        authCodeDTO.setTempAuthCode("123123123");

        //when
        userRedisService.saveVerificationCode(authCodeDTO.getUserId(), authCodeDTO.getTempAuthCode());
        boolean isVerified = userRedisService.verifyCode(authCodeDTO.getUserId(), authCodeDTO.getTempAuthCode());

        //then
        assertThat(isVerified).isTrue();
        assertThatCode(() -> userRedisService.saveVerifiedStatus(authCodeDTO.getUserId()))
                .doesNotThrowAnyException();
    }

    @Test
    void 비밀번호_변경_인증번호_체크_실패(){
        //given
        AuthCodeDTO authCodeDTO = new AuthCodeDTO();
        authCodeDTO.setUserId("asd123");
        authCodeDTO.setTempAuthCode("123123123");

        //when
        userRedisService.saveVerificationCode(authCodeDTO.getUserId(), "인증번호가 없거나 다를경우");
        boolean isVerified = userRedisService.verifyCode(authCodeDTO.getUserId(), authCodeDTO.getTempAuthCode());

        //then
        assertThat(isVerified).isFalse();
    }

}