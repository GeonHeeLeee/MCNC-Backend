package mcnc.survwey.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.LoginDTO;
import mcnc.survwey.domain.user.service.AuthService;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;

import static mcnc.survwey.global.config.AuthInterceptor.LOGIN_USER;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthService authService;

    private LoginDTO validLoginDTO;
    private User validUser;

    @BeforeEach
    void setUp() {
        // 테스트에 사용될 기본 데이터 설정
        validLoginDTO = new LoginDTO("testUser", "correctPassword");
        validUser = User.builder()
                .userId("testUser")
                .password(passwordEncoder.encode("correctPassword"))
                .build();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginUser_success() {
        // Given
        when(userService.findByUserId(validLoginDTO.getUserId())).thenReturn(validUser);
        when(passwordEncoder.matches(validLoginDTO.getPassword(), validUser.getPassword())).thenReturn(true);
        when(request.getSession()).thenReturn(session);

        // When
        authService.loginUser(validLoginDTO, request);

        // Then
        // verify() 메서드는 Mockito에서 특정 메서드가 예상된 방식으로 호출되었는지 검증하는 데 사용
        /**
         * verify(session): session 객체의 메서드 호출을 검증
         * .setAttribute(): 특정 메서드(setAttribute)가 호출되었는지 확인
         * eq(LOGIN_USER): 첫 번째 인자가 LOGIN_USER 상수와 정확히 일치하는지
         * eq(validLoginDTO.getUserId()): 두 번째 인자가 로그인 DTO의 userId와 정확히 일치하는지
         */
        verify(session).setAttribute(eq(LOGIN_USER), eq(validLoginDTO.getUserId()));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 로그인 시도 테스트")
    void loginUser_userNotFound() {
        // Given
        when(userService.findByUserId(validLoginDTO.getUserId())).thenThrow(CustomException.class);

        // When & Then
        assertThrows(CustomException.class, () -> {
            authService.loginUser(validLoginDTO, request);
        }, "사용자를 찾을 수 없을 때 CustomException이 발생해야 함");
    }

    @Test
    @DisplayName("잘못된 비밀번호 로그인 시도 테스트")
    void loginUser_invalidPassword() {
        // Given
        when(userService.findByUserId(validLoginDTO.getUserId())).thenReturn(validUser);
        when(passwordEncoder.matches(validLoginDTO.getPassword(), validUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(CustomException.class, () -> {
            authService.loginUser(validLoginDTO, request);
        }, "비밀번호가 일치하지 않을 때 CustomException이 발생해야 함");
    }
}