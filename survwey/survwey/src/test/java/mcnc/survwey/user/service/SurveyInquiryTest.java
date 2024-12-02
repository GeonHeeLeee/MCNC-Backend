package mcnc.survwey.user.service;

import mcnc.survwey.domain.enums.Gender;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.selection.SelectionId;
import mcnc.survwey.domain.selection.dto.SelectionResultDTO;
import mcnc.survwey.domain.selection.repository.SelectionRepository;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
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

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SelectionRepository selectionRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyInquiryService surveyInquiryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser1;
    private User testUser2;
    private Survey survey1;
    private Survey survey2;

    @BeforeEach
    void setUp() {
        testUser1 = createTestUser("testUser1", "testUser1@test.com", Gender.M);
        testUser2 = createTestUser("testUser2", "testUser2@test.com", Gender.F);

        survey1 = createTestSurvey(testUser1, "survey1");
        survey2 = createTestSurvey(testUser1, "survey2");

        userRepository.save(testUser1);
        userRepository.save(testUser2);
        surveyRepository.save(survey1);
        surveyRepository.save(survey2);
    }

    @Test
    @DisplayName("사용자가 만든 설문 조회 성공 테스트")
    public void testSuccessfulGetUserCreatedSurveyList() {
        // When
        Page<SurveyWithCountDTO> result = surveyInquiryService.getUserCreatedSurveyList(
                testUser1.getUserId(), PAGE_NUMBER, PAGE_SIZE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("survey1", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("사용자가 만든 설문 조회 실패 테스트")
    public void testFailedGetUserCreatedSurveyList() {
        // When
        Page<SurveyWithCountDTO> result = surveyInquiryService.getUserCreatedSurveyList(
                testUser2.getUserId(), PAGE_NUMBER, PAGE_SIZE);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("특정 설문 조회 성공 테스트")
    public void testSuccessfulGetSurveyWithDetail() {
        // Given
        Question question1 = createTestQuestion(survey1, "질문1", QuestionType.SUBJECTIVE);
        Question question2 = createTestQuestion(survey1, "질문2", QuestionType.OBJ_SINGLE);

        questionRepository.save(question1);
        questionRepository.save(question2);

        Selection selection1 = createTestSelection(question2, "선택1", false);
        Selection selection2 = createTestSelection(question2, "선택2", true);

        selectionRepository.save(selection1);
        selectionRepository.save(selection2);
        surveyRepository.save(survey1);

        // When
        SurveyWithDetailDTO surveyWithDetail = surveyInquiryService.getSurveyWithDetail(survey1.getSurveyId());

        // Then
        assertEquals(surveyWithDetail.getSurveyId(), survey1.getSurveyId());
        assertEquals(surveyWithDetail.getQuestionList().size(), 2);
        assertEquals(surveyWithDetail.getQuestionList().get(1).getSelectionList().size(), 2);
    }

//    @Test
//    @DisplayName("특정 설문 조회 실패 테스트 - 설문 아이디 존재하지 않음")
//    //Giv

    private User createTestUser(String userId, String email, Gender gender) {
        return User.builder()
                .userId(userId)
                .name(userId)
                .email(email)
                .password(passwordEncoder.encode(userId + "Pass"))
                .birth(LocalDate.now())
                .registerDate(LocalDateTime.now())
                .gender(gender)
                .build();
    }

    private Survey createTestSurvey(User user, String title) {
        return Survey.builder()
                .expireDate(LocalDateTime.now().plusDays(30))
                .title(title)
                .description(title + " description")
                .user(user)
                .build();
    }

    private Question createTestQuestion(Survey survey, String body, QuestionType type) {
        Question question = Question.builder()
                .body(body)
                .survey(survey)
                .type(type)
                .build();
        survey.addQuestion(question);
        return question;
    }

    private Selection createTestSelection(Question question, String body, boolean isEtc) {
        Selection selection = Selection.builder()
                .id(new SelectionId(question.getQuesId(), question.getSelectionList().size()))
                .body(body)
                .isEtc(isEtc)
                .question(question)
                .build();
        question.addSelection(selection);
        return selection;
    }
}