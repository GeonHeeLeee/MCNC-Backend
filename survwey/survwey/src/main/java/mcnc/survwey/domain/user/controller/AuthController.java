package mcnc.survwey.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.user.dto.LoginDTO;
import mcnc.survwey.domain.user.service.AccountService;
import mcnc.survwey.domain.user.service.AuthService;
import mcnc.survwey.global.exception.custom.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "사용자 인증 관련", description = "로그인/로그아웃 관련 API")
public class AuthController {


    private final AuthService authService;

    /**
     * 사용자 로그인
     * 로그인 성공시 세션으로 저장
     *
     * @param loginDTO
     * @param request
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "사용자 로그인", description = "userId, password를 응답에 받아 주면 됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = """
                    잘못된 요청:
                    - userId 또는 password 불일치 - errorMessage : 아이디/비밀번호가 일치하지 않습니다."
                    """)
    })
    public ResponseEntity<Object> loginUser(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) {
        try {
            authService.loginUser(loginDTO, request);
            return ResponseEntity.ok().body(null);
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(Map.of("errorMessage", "아이디/비밀번호가 일치하지 않습니다."));
        }
    }

    /**
     * 사용자 로그아웃
     * - 요청 시 세션이 유효하면 200
     * - 세션이 유효하지 않으면 401
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "사용자 로그아웃", description = "요청 Body 없이 요청<br>세션이 유효하면 세션 값 파기, 세션이 없거나 유효하지 않으면 401 응답")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "message : 로그아웃 성공")
    })
    public ResponseEntity<Map<String, String>> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return ResponseEntity.ok().body(Map.of("message", "로그아웃 성공"));
    }


    /**
     * 프론트 세션 체크
     * - 세션이 유효하지 않을 시 로그인 화면으로 리다이렉션 용
     * @return
     */
    @GetMapping("/session")
    @Operation(summary = "세션 체크", description = "세션이 유효한지 체크")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "세션 유효"),
            @ApiResponse(responseCode = "401", description = "세션이 유효하지 않음")
    })
    public ResponseEntity<Object> checkSession() {
        return ResponseEntity.ok(null);
    }


    //비밀번호 변경 요청(이메일 전송 및 토큰 생성)

    //클릭한 링크에 대해 토큰이 유효한지 검증하는 메서드
}
