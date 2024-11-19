package mcnc.survwey.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.user.dto.AuthDTO;
import mcnc.survwey.domain.user.dto.ChangePasswordDTO;
import mcnc.survwey.domain.user.dto.LoginDTO;
import mcnc.survwey.domain.user.dto.ModifyDTO;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.repository.UserRepository;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static mcnc.survwey.global.config.AuthInterceptor.LOGIN_USER;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    /**
     * 회원 가입 메서드
     *
     * @param authDTO
     */

    public void registerUser(AuthDTO authDTO) {
        if (userRepository.existsById(authDTO.getUserId())) {//해당 아이디 이미 존재
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_ID_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(authDTO.getEmail())) {//해당 이메일 존재
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }
        
        //ID, EMAIL 중복이 없을 경우 저장
        userRepository.save(User.builder()
                .userId(authDTO.getUserId())
                .email(authDTO.getEmail())
                .password(passwordEncoder.encode(authDTO.getPassword()))
                .name(authDTO.getName())
                .registerDate(LocalDateTime.now())
                .birth(authDTO.getBirth())
                .gender(authDTO.getGender())
                .build()
        );
    }

    /**
     * id, email 중복 검증
     * @param userId
     * @param email
     * @return
     */
    public Map<String, Boolean> duplicatedUserNameAndEmail(String userId, String email){
        Map<String, Boolean> map = new HashMap<>();

        map.put("id", userRepository.existsById(userId));
        map.put("email", userRepository.existsByEmail(email));

        return map;
    }

    /**
     * 프로필 수정
     *
     * @param modifyDTO
     */
    public void modifyUser(ModifyDTO modifyDTO, String userId) {
        User user = userService.findByUserId(userId);

        if (modifyDTO.getName() == null || modifyDTO.getName().isEmpty() || modifyDTO.getName().isBlank()) {
            modifyDTO.setName(user.getName());
        }
        if (modifyDTO.getGender() == null || modifyDTO.getGender().getValue().isEmpty()
                || modifyDTO.getGender().getValue().isBlank()) {
            modifyDTO.setGender(user.getGender());
        }
        if (modifyDTO.getBirth() == null) {
            modifyDTO.setBirth(user.getBirth());
        }
        //사용자가 특정 항목을 수정하지 않을 시 원래 user 정보를 가져옴
        
        user.setName(modifyDTO.getName());
        user.setGender(modifyDTO.getGender());
        user.setBirth(modifyDTO.getBirth());

        userRepository.save(user);
    }


    /**
     * 비밀번호 변경
     *
     * @param changePasswordDTO
     */
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        User user = userService.findByUserId(changePasswordDTO.getUserId());
        //사용자 Id 찾은 후
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
        //사용자 password 재설정
        userRepository.save(user);
    }


    public void loginAndCreateSession(LoginDTO loginDTO, HttpServletRequest request) {
        User foundUser = Optional.ofNullable(userService.findByUserId(loginDTO.getUserId()))
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(loginDTO.getPassword(), foundUser.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, loginDTO.getUserId());
    }
}
