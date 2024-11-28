package mcnc.survwey.user.service;

import io.github.cdimascio.dotenv.Dotenv;
import mcnc.survwey.domain.enums.Gender;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.AuthDTO;
import mcnc.survwey.domain.user.dto.ProfileDTO;
import mcnc.survwey.domain.user.dto.ProfileModifyDTO;
import mcnc.survwey.domain.user.service.AccountService;
import mcnc.survwey.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @BeforeAll
    static void setup() {
        // .env 파일을 로드해서 환경 변수 설정
        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("MAIL_USER_NAME", dotenv.get("MAIL_USER_NAME"));
        System.setProperty("MAIL_USER_PASSWORD", dotenv.get("MAIL_USER_PASSWORD"));
    }
    @Test
    public void 회원가입(){

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUserId("test123");
        authDTO.setEmail("test@test.com");
        authDTO.setPassword("qwer1234@@");
        authDTO.setName("tester");
        authDTO.setBirth(LocalDate.now());
        authDTO.setGender(Gender.M);

        accountService.registerUser(authDTO);
        User user = userService.findByUserId(authDTO.getUserId());

        System.out.println("user.getEmail() = " + user.getEmail());

        assertThat(user.getUserId()).isEqualTo(authDTO.getUserId());

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