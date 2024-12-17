package mcnc.survwey.user.service;

import mcnc.survwey.api.account.dto.PasswordModifyDTO;
import mcnc.survwey.domain.user.enums.Gender;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.api.account.dto.ProfileDTO;
import mcnc.survwey.api.account.dto.ProfileModifyDTO;
import mcnc.survwey.api.account.dto.RegisterDTO;
import mcnc.survwey.api.account.service.AccountService;
import mcnc.survwey.domain.user.service.UserRedisService;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.utils.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.offset;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Autowired
    private UserRedisService userRedisService;

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
    }


    @Test
    public void 회원가입_성공(){
        //given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUserId("test123");
        registerDTO.setEmail("ccJ2ky8W1P-xITy45CREINOVq71sKDTABKPUnVjiwDc");
        registerDTO.setName("tester");
        registerDTO.setPassword("-b4tdMhq4MAQpTFwIRLVRg");
        registerDTO.setBirth(LocalDate.now());
        registerDTO.setGender(Gender.M);

        //when
        accountService.registerUser(registerDTO);
        User user = userService.findByUserId(registerDTO.getUserId());
        boolean userPassword = passwordEncoder.matches(registerDTO.getPassword(), user.getPassword());

        //then
        assertThat(user).usingRecursiveComparison()
                .ignoringFields("registerDate", "respondList", "password")
                .isEqualTo(registerDTO);
        assertThat(userPassword).isTrue();
    }
    @Test
    public void 사용자_프로필_수정(){
        //given
        ProfileModifyDTO profileModifyDTO = ProfileModifyDTO.builder()
                .name("프로필_수정_테스트")
                .email("ProfileModifyTest@test.com")
                .build();

        User user = userService.findByUserId("asd123");

        //when
        accountService.modifyUserProfile(profileModifyDTO, user.getUserId());

        //then
        assertThat(user.getEmail()).isEqualTo(profileModifyDTO.getEmail());
        assertThat(user.getName()).isEqualTo(profileModifyDTO.getName());
    }

    @Test
    public void 사용자_프로필_조회(){
        //given
        User user = userService.findByUserId("asd123");

        //when
        ProfileDTO profileDTO = accountService.getProfile(user.getUserId());

        //then
        assertThat(user).usingRecursiveComparison()
                .ignoringFields("registerDate", "respondList", "password", "email")
                .isEqualTo(profileDTO);
        assertThat(user.getEmail()).isEqualTo(encryptionUtil.decrypt(profileDTO.getEmail()));
    }

    @Test
    public void 사용자_비밀번호_변경_성공(){
        //given
        User user = userService.findByUserId("asd123");

        PasswordModifyDTO passwordModifyDTO = new PasswordModifyDTO();
        passwordModifyDTO.setUserId("asd123");
        passwordModifyDTO.setPassword("zxcv1234@!");

        //when
        boolean isChecked = userRedisService.isVerified(user.getUserId());
        accountService.modifyPassword(user.getUserId(), passwordModifyDTO.getPassword());
        boolean isChange = passwordEncoder.matches(passwordModifyDTO.getPassword(), user.getPassword());

        //then
        assertThat(isChecked).isFalse();
        assertThat(isChange).isTrue();
    }

}