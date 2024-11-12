package mcnc.survwey.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.user.dto.AuthDTO;
import mcnc.survwey.domain.user.dto.ChangePasswordDTO;
import mcnc.survwey.domain.user.dto.LoginDTO;
import mcnc.survwey.domain.user.dto.ModifyDTO;
import mcnc.survwey.domain.user.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RequiredArgsConstructor
@RestController("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<Object> register(@Valid @RequestBody AuthDTO authDTO) {
        authService.registerUser(authDTO);
        return ResponseEntity.ok(authDTO.getUserId());
    }

    @PostMapping("/modify/profile")
    public ResponseEntity<Object> modify(@Valid @RequestBody ModifyDTO modifyDTO) {
        authService.modifyUser(modifyDTO);
        return ResponseEntity.ok(modifyDTO.getUserId());
    }

    @PostMapping("/modify/password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        authService.changePassword(changePasswordDTO);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) {
        boolean loginResult = authService.loginAndCreateSession(loginDTO, request);
        if (loginResult) {
            return ResponseEntity.ok(Collections.singletonMap("userId", loginDTO.getUserId()));
        }
        return ResponseEntity.badRequest().body(null);
    }

}
