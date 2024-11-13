package mcnc.survwey.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.user.dto.AuthDTO;
import mcnc.survwey.domain.user.dto.ChangePasswordDTO;
import mcnc.survwey.domain.user.dto.LoginDTO;
import mcnc.survwey.domain.user.dto.ModifyDTO;
import mcnc.survwey.domain.user.service.AuthService;
import mcnc.survwey.global.config.SessionContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "사용자 관련", description = "회원가입/로그인/프로필 수정 API")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원 가입 로직
     * -중복 ID, Email 존재 시 에러 메시지
     * @param authDTO
     * @return
     */
    @PostMapping("/join")
    public ResponseEntity<Object> register(@Valid @RequestBody AuthDTO authDTO) {
        authService.registerUser(authDTO);
        return ResponseEntity.ok(authDTO.getUserId());
    }

    /**
     * 프로필 수정 로직
     * 사용자 이름, 생일, 성별 변경
     * @param modifyDTO
     * @return
     */
    @PostMapping("/modify/profile")
    public ResponseEntity<Object> modify(@Valid @RequestBody ModifyDTO modifyDTO) {
        String userId = SessionContext.getCurrentUser();
        authService.modifyUser(modifyDTO, userId);
        return ResponseEntity.ok(userId);
    }

    /**
     * 사용자 비밀번호 변경 로직
     * 해당 사용자의 ID를 조회 후 변경
     * @param changePasswordDTO
     * @return
     */
    @PostMapping("/modify/password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        authService.changePassword(changePasswordDTO);
        return ResponseEntity.ok(null);
    }

    /**
     * 사용자 로그인
     * 로그인 성공시 세션으로 저장
     * @param loginDTO
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) {
        boolean loginResult = authService.loginAndCreateSession(loginDTO, request);
        if (loginResult) {
            return ResponseEntity.ok(Collections.singletonMap("userId", loginDTO.getUserId()));
        }
        return ResponseEntity.badRequest().body(null);
    }

}
