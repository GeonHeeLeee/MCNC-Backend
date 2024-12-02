package mcnc.survwey.user.service;

import mcnc.survwey.domain.enums.Gender;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.inquiry.service.SurveyInquiryService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional //테스트 후 롤백
public class SurveyInquiryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyInquiryService surveyInquiryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Survey survey1;
    private Survey survey2;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = User.builder()
                .userId("testUser1")
                .name("testUser1")
                .email("testUser1@test.com")
                .password(passwordEncoder.encode("test1"))
                .birth(LocalDate.from(LocalDateTime.now()))
                .registerDate(LocalDateTime.now())
                .gender(Gender.M)
                .build();

        testUser2 = User.builder()
                .userId("testUser2")
                .name("testUser2")
                .password(passwordEncoder.encode("test2"))
                .email("testUser2@test.com")
                .birth(LocalDate.from(LocalDateTime.now()))
                .registerDate(LocalDateTime.now())
                .gender(Gender.F)
                .build();

        survey1 = Survey.builder()
                .expireDate(LocalDateTime.now().plusDays(30))
                .title("survey1")
                .description("survey1 description")
                .user(testUser1)
                .build();

        survey2 = Survey.builder()
                .expireDate(LocalDateTime.now().plusDays(30))
                .title("survey2")
                .description("survey2 description")
                .user(testUser1)
                .build();

        userRepository.save(testUser1);
        userRepository.save(testUser2);
        surveyRepository.save(survey1);
        surveyRepository.save(survey2);
    }

    @Test
    @DisplayName("사용자가 만든 설문 조회 성공 테스트")
    public void 사용자가_만든_설문_조회_성공_테스트() {
        //when
        Page<SurveyWithCountDTO> result = surveyInquiryService.getUserCreatedSurveyList(
                testUser1.getUserId(), 0, 10);

        //then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("survey1", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("사용자가 만든 설문 조회 실패 테스트")
    public void 사용자가_만든_설문_조회_실패_테스트() {
        //when
        Page<SurveyWithCountDTO> result = surveyInquiryService.getUserCreatedSurveyList(
                testUser2.getUserId(), 0, 10);

        //then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("특정 설문 조회 성공 테스트")
    public void 특정_설문_조회_성공_테스트() {
        //given
        Question question = Question.builder()
                .body("질문1")
                .survey(survey1)
                .type(QuestionType.SUBJECTIVE)
                .build();

        for()
        Selection selection = Selection.builder()
                .body("선택1")
                .isEtc(false)
                .question(question)
                .build();
        //when

        //then
    }
}
