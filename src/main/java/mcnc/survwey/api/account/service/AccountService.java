package mcnc.survwey.api.account.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.account.dto.RegisterDTO;

import mcnc.survwey.api.account.dto.ProfileDTO;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.api.account.dto.ProfileModifyDTO;
import mcnc.survwey.domain.user.repository.UserRepository;
import mcnc.survwey.domain.user.service.UserRedisService;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.utils.EncryptionUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static mcnc.survwey.global.exception.custom.ErrorCode.USER_EMAIL_ALREADY_EXISTS;
import static mcnc.survwey.global.exception.custom.ErrorCode.USER_ID_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserRedisService userRedisService;
    private final EncryptionUtil encryptionUtil;

    /**
     * 회원가입 메소드
     *
     * @param registerDTO
     */
    public void registerUser(RegisterDTO registerDTO) {
        //해당 아이디 이미 존재
        if (userRepository.existsById(registerDTO.getUserId())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, USER_ID_ALREADY_EXISTS);
        }
        //해당 이메일 존재
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, USER_EMAIL_ALREADY_EXISTS);
        }

        User newUser = registerDTO.toEntity(passwordEncoder.encode(registerDTO.getPassword()));
        //ID, EMAIL 중복이 없을 경우 저장
        userRepository.save(newUser);
    }

    /**
     * id 중복 검증
     *
     * @param userId
     * @return ture: 중복, false: 중복 X front에 전송
     */
    public Map<String, Boolean> validateDuplicatedUserId(String userId) {
        //id, email map에 저장 후 true일 경우 중복 
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicated", userRepository.existsById(userId));
        return response;
    }

    /**
     * 프로필 수정
     *
     * @param profileModifyDTO
     * @param userId
     */
    public void modifyUserProfile(ProfileModifyDTO profileModifyDTO, String userId) {
        User user = userService.findByUserId(userId);
        //프로필 null, 빈칸, 띄어쓰기만 입력될 경우
        if (!StringUtils.hasText(profileModifyDTO.getName())) {
            profileModifyDTO.setName(user.getName());
        }
        if (!StringUtils.hasText(profileModifyDTO.getEmail())
                || user.getEmail().equals(profileModifyDTO.getEmail())) {
            //사용자가 이메일을 변경하지 않았을 경우 기존에 이메일 유지
            profileModifyDTO.setEmail(user.getEmail());
        } else if (userRepository.existsByEmail(profileModifyDTO.getEmail())) {
            //이메일이 중복일 경우
            throw new CustomException(HttpStatus.BAD_REQUEST, USER_EMAIL_ALREADY_EXISTS);
        }
        //사용자가 특정 항목을 수정하지 않을 시 원래 user 정보를 가져옴
        //아이디, 성별, 생일은 변경하지 않음
        user.modifyUser(profileModifyDTO.getName(), profileModifyDTO.getEmail());
    }

    /**
     * 사용자 프로필 정보 조회
     *
     * @param userId
     * @return
     */
    public ProfileDTO getProfile(String userId) {
        User user = userService.findByUserId(userId);
        return ProfileDTO.of(user, encryptionUtil.encryptText(user.getEmail()));
    }

    /**
     * 비밀번호 변경
     * - 비밀번호 변경 후 Redis에 저장된 인증 성공 상태 삭제
     *
     * @param userId
     * @param password
     */
    public void modifyPassword(String userId, String password) {
        User user = userService.findByUserId(userId);
        //사용자 ID 존재 확인 후 비밀번호 변경
        user.modifyPassword(passwordEncoder.encode(password));
        userRedisService.deleteVerifiedStatus(userId);
    }
}
