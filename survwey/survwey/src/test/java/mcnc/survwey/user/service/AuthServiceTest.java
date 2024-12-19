package mcnc.survwey.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mcnc.survwey.api.account.dto.RegisterDTO;
import mcnc.survwey.api.account.service.AccountService;
import mcnc.survwey.api.auth.dto.AuthCodeUserIdDTO;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.api.auth.dto.LoginDTO;
import mcnc.survwey.api.auth.service.AuthService;
import mcnc.survwey.domain.user.enums.Gender;
import mcnc.survwey.domain.user.service.UserRedisService;
import mcnc.survwey.domain.user.service.UserService;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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
        AuthCodeUserIdDTO authCodeUserIdDTO = new AuthCodeUserIdDTO();
        authCodeUserIdDTO.setUserId("asd123");
        authCodeUserIdDTO.setTempAuthCode("123123123");

        //when
        userRedisService.saveVerificationCode(authCodeUserIdDTO.getUserId(), authCodeUserIdDTO.getTempAuthCode());
        boolean isVerified = userRedisService.isCodeVerified(authCodeUserIdDTO.getUserId(), authCodeUserIdDTO.getTempAuthCode());

        //then
        assertThat(isVerified).isTrue();
        assertThatCode(() -> userRedisService.saveVerifiedStatus(authCodeUserIdDTO.getUserId()))
                .doesNotThrowAnyException();
    }

    @Test
    void 비밀번호_변경_인증번호_체크_실패(){
        //given
        AuthCodeUserIdDTO authCodeUserIdDTO = new AuthCodeUserIdDTO();
        authCodeUserIdDTO.setUserId("asd123");
        authCodeUserIdDTO.setTempAuthCode("123123123");

        //when
        userRedisService.saveVerificationCode(authCodeUserIdDTO.getUserId(), "인증번호가 없거나 다를경우");
        boolean isVerified = userRedisService.isCodeVerified(authCodeUserIdDTO.getUserId(), authCodeUserIdDTO.getTempAuthCode());

        //then
        assertThat(isVerified).isFalse();
    }

}