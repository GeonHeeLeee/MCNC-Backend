package mcnc.survwey.survey;

import mcnc.survwey.domain.enums.Gender;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.inquiry.service.SurveyInquiryService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.util.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional //테스트 후 롤백
public class SurveyInquiryTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Autowired
    private SurveyInquiryService surveyInquiryService;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private TestDataFactory testDataFactory;


    @Test
    @DisplayName("사용자가 만든 설문 조회 성공 테스트")
    public void testSuccessfulGetUserCreatedSurveyList() {
        //given
        testDataFactory.setupTestData();

        // When
        Page<SurveyWithCountDTO> result = surveyInquiryService.getUserCreatedSurveyList("testUser1", PAGE_NUMBER, PAGE_SIZE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("survey1", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("사용자가 만든 설문 조회 실패 테스트 - 사용자가 만든 설문 없음")
    public void testFailedGetUserCreatedSurveyList() {
        //given
        testDataFactory.setupTestData();

        // When
        Page<SurveyWithCountDTO> result = surveyInquiryService.getUserCreatedSurveyList(
                "testUser2", PAGE_NUMBER, PAGE_SIZE);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("특정 설문 조회 성공 테스트")
    public void testSuccessfulGetSurveyWithDetail() {
        // Given
        User user = testDataFactory.createUser("testUser1", "testUser1@test.com", "test1", Gender.M);
        testDataFactory.saveUser(user);
        Survey survey = testDataFactory.createSurvey("survey1", "survey1 description", user);
        testDataFactory.saveSurvey(survey);

        Question question1 = testDataFactory.createQuestion("질문1", QuestionType.SUBJECTIVE, survey);
        Question question2 = testDataFactory.createQuestion("질문2", QuestionType.OBJ_SINGLE, survey);

        survey.addQuestion(question1);
        survey.addQuestion(question2);

        testDataFactory.saveQuestion(question1);
        testDataFactory.saveQuestion(question2);

        Selection selection1 = testDataFactory.createSelection("선택1", false, question2, 0);
        Selection selection2 = testDataFactory.createSelection("선택2", true, question2, 1);

        question2.addSelection(selection1);
        question2.addSelection(selection2);


        testDataFactory.saveSelection(selection1);
        testDataFactory.saveSelection(selection2);


        // When
        SurveyWithDetailDTO surveyWithDetail = surveyInquiryService.getSurveyWithDetail(survey.getSurveyId());

        // Then
        assertEquals(surveyWithDetail.getSurveyId(), survey.getSurveyId());
        assertEquals(surveyWithDetail.getQuestionList().size(), 2);
        assertEquals(surveyWithDetail.getQuestionList().get(1).getSelectionList().size(), 2);
    }

    @Test
    @DisplayName("특정 설문 조회 실패 테스트 - 설문 아이디 존재하지 않음")
    public void testFailedGetSurveyWithDetail() {
        // Given
        long notExistingSurveyId = Integer.MAX_VALUE;
        // When-Then
        assertThrows(CustomException.class, () -> surveyInquiryService.getSurveyWithDetail(notExistingSurveyId));
    }

    @Test
    @DisplayName("본인이 만든 설문 조사 테스트 - 본인이 생성한 설문")
    public void testIsSurveyUserMade_True() {
        //Given
        User testUser1 = testDataFactory.createUser("testUser1", "testUser1@test.com", "test1", Gender.M);
        Survey survey1 = testDataFactory.createSurvey("survey1", "survey1 description", testUser1);

        testDataFactory.saveUser(testUser1);
        testDataFactory.saveSurvey(survey1);

        //When
        Map<String, Boolean> result = surveyInquiryService.isSurveyUserMade("testUser1", survey1.getSurveyId());

        //Then
        assertNotNull(result.get("result"));
        assertEquals(result.get("result"), true);
    }

    @Test
    @DisplayName("본인이 만든 설문 조사 테스트 - 본인이 생성하지 않은 설문")
    public void testIsSurveyUserMade_False() {
        //Given
        User testUser1 = testDataFactory.createUser("testUser1", "testUser1@test.com", "test1", Gender.M);
        User testUser2 = testDataFactory.createUser("testUser2", "testUser2@test.com", "test2", Gender.M);

        Survey survey1 = testDataFactory.createSurvey("survey1", "survey1 description", testUser1);

        testDataFactory.saveUser(testUser1);
        testDataFactory.saveUser(testUser2);
        testDataFactory.saveSurvey(survey1);

        //When
        Map<String, Boolean> result = surveyInquiryService.isSurveyUserMade("testUser2", survey1.getSurveyId());

        //Then
        assertNotNull(result.get("result"));
        assertEquals(result.get("result"), false);
    }

}