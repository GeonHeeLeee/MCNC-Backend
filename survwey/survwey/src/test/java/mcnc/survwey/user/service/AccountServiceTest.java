package mcnc.survwey.user.service;

import mcnc.survwey.domain.user.enums.Gender;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.api.account.dto.ProfileDTO;
import mcnc.survwey.api.account.dto.ProfileModifyDTO;
import mcnc.survwey.api.account.dto.RegisterDTO;
import mcnc.survwey.api.account.service.AccountService;
import mcnc.survwey.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    private Environment environment;

    @Test
    void checkActiveProfile() {
        System.out.println("Active Profiles: " + Arrays.toString(environment.getActiveProfiles()));
    }
    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;


    @Test
    public void 회원가입(){

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUserId("test123");
        registerDTO.setEmail("test@test.com");
        registerDTO.setPassword("qwer1234@@");
        registerDTO.setName("tester");
        registerDTO.setBirth(LocalDate.now());
        registerDTO.setGender(Gender.M);

        accountService.registerUser(registerDTO);
        User user = userService.findByUserId(registerDTO.getUserId());

        System.out.println("user.getEmail() = " + user.getEmail());

        assertThat(user.getUserId()).isEqualTo(registerDTO.getUserId());

    }

    @Test
    public void 사용자_프로필_수정(){
        ProfileModifyDTO profileModifyDTO = ProfileModifyDTO.builder()
                .name("프로필_수정_테스트")
                .email("ProfileModifyTest@test.com")
                .build();

        User user = User.builder()
                .userId("test123")
                .email("test@test.com")
                .password("qwer1234@@")
                .name("tester")
                .birth(LocalDate.now())
                .gender(Gender.M).build();

        accountService.modifyUserProfile(profileModifyDTO, user.getUserId());

        assertThat(user.getName()).isEqualTo(profileModifyDTO.getName());
    }

    @Test
    public void 사용자_프로필_조회(){

        User user = User.builder()
                .userId("test123")
                .email("getprofile@test.com")
                .name("사용자_프로필_조회")
                .password("qwer1234@@")
                .gender(Gender.M)
                .birth(LocalDate.now())
                .build();

        ProfileDTO profileDTO = accountService.getProfile(user.getUserId());

        assertThat(profileDTO.getName()).isEqualTo(user.getName());
    }
}