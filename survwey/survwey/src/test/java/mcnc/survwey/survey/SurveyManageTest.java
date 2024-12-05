package mcnc.survwey.survey;

import mcnc.survwey.domain.question.enums.QuestionType;
import mcnc.survwey.api.survey.manage.dto.QuestionDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.manage.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.repository.SurveyRepository;
import mcnc.survwey.domain.survey.service.SurveyRedisService;
import mcnc.survwey.api.survey.manage.service.SurveyManageService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.repository.UserRepository;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.util.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static mcnc.survwey.domain.question.enums.QuestionType.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional //테스트 후 롤백
public class SurveyManageTest extends BaseIntegrationTest {

    @Autowired
    private SurveyManageService surveyManageService;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyRedisService surveyRedisService;

    @Test
    @DisplayName("설문 생성 성공 테스트")
    public void testSuccessSaveSurveyWithDetail() {
        //Given
        String userId = "testUser1";
        SurveyWithDetailDTO surveyWithDetailDTO = setUpSaveSurveyWithDetailData(userId);

        //When
        Survey createdSurvey = surveyManageService.saveSurveyWithDetails(surveyWithDetailDTO, userId);
        boolean isSurveyKeyExists = surveyRedisService.isSurveyKeyExist(userId, createdSurvey.getSurveyId());

        //Then
        assertEquals(createdSurvey.getQuestionList().size(), 3);
        assertEquals(createdSurvey.getQuestionList().get(1).getSelectionList().size(), 2);
        assertTrue(createdSurvey.getQuestionList().get(2).getSelectionList().get(1).isEtc());
        assertTrue(isSurveyKeyExists);
        assertEquals(createdSurvey.getUser().getUserId(), userId);
    }

    @Test
    @DisplayName("설문 생성 실패 테스트 - 사용자가 존재하지 않음")
    public void testFailedSaveSurveyWithDetail_UserNotExists() {
        //Given
        String userId = "notExistingUser";
        SurveyWithDetailDTO surveyWithDetailDTO = setUpSaveSurveyWithDetailData(userId);

        //When-Then
        assertThrows(CustomException.class, () -> surveyManageService.saveSurveyWithDetails(surveyWithDetailDTO, userId));
    }

    @Test
    @DisplayName("설문 삭제 성공 테스트")
    public void testSuccessDeleteSurveyAfterValidation() {
        //Given
        String creatorId = "testUser1";
        Survey existingSurvey = surveyRepository.findById(1L).get();

        //When - Then
        assertDoesNotThrow(() -> {
            surveyManageService.deleteSurveyAfterValidation(creatorId, existingSurvey.getSurveyId());
        });
        assertFalse(surveyRedisService.isSurveyKeyExist(creatorId, existingSurvey.getSurveyId()));
        assertTrue(surveyRepository.findById(existingSurvey.getSurveyId()).isEmpty());
    }

    @Test
    @DisplayName("설문 삭제 실패 테스트 - 요청자가 만든 설문이 아님")
    public void testFailedDeleteSurveyAfterValidation_NotRequesterMade() {
        //Given
        String userId = "testUser2";
        Survey existingSurvey = surveyRepository.findById(1L).get();

        //When - Then
        assertThrows(CustomException.class, () -> surveyManageService.deleteSurveyAfterValidation(userId, existingSurvey.getSurveyId()));
    }

    @Test
    @DisplayName("설문 수정 성공 테스트")
    public void testSuccessModifySurvey() {
        //Given
        String userId = "testUser1";
        Survey survey = surveyRepository.findById(2L).get();
        SurveyWithDetailDTO surveyWithDetailDTO = SurveyWithDetailDTO.of(survey);
        Long existingQuesId = surveyWithDetailDTO.getQuestionList().get(0).getQuesId();

        String modifiedTitle = "modified title";
        String modifiedDescription = "modified description";
        LocalDateTime modifiedExpireDate = LocalDateTime.of(2030,11,11,11,11);

        surveyWithDetailDTO.setTitle(modifiedTitle);
        surveyWithDetailDTO.setDescription(modifiedDescription);
        surveyWithDetailDTO.setExpireDate(modifiedExpireDate);

        //When
        SurveyWithDetailDTO modifiedResult = surveyManageService.modifySurvey(surveyWithDetailDTO, userId);

        //Then
        assertEquals(modifiedResult.getSurveyId(), survey.getSurveyId());
        assertEquals(modifiedResult.getTitle(), modifiedTitle);
        assertEquals(modifiedResult.getDescription(), modifiedDescription);
        assertEquals(modifiedResult.getExpireDate(), modifiedExpireDate);
        assertNotEquals(modifiedResult.getQuestionList().get(0).getQuesId(),
                existingQuesId);
    }

    private static SurveyWithDetailDTO setUpSaveSurveyWithDetailData(String userId) {
        SurveyWithDetailDTO surveyDTO = buildSurveyWithDetailDTO(userId);

        QuestionDTO question1 = buildQuestionDTO("질문 1", SUBJECTIVE);
        QuestionDTO question2 = buildQuestionDTO("질문 2", OBJ_MULTI);
        QuestionDTO question3 = buildQuestionDTO("질문 3", OBJ_SINGLE);

        QuestionDTO.SelectionDTO selection1 = buildSelectionDTO("보기 1", false);
        QuestionDTO.SelectionDTO selection2 = buildSelectionDTO("보기 2", false);
        QuestionDTO.SelectionDTO selection3 = buildSelectionDTO("보기 3", false);
        QuestionDTO.SelectionDTO selection4 = buildSelectionDTO("보기 4 - 기타", true);

        surveyDTO.getQuestionList().add(question1);
        surveyDTO.getQuestionList().add(question2);
        surveyDTO.getQuestionList().add(question3);
        question2.getSelectionList().add(selection1);
        question2.getSelectionList().add(selection2);
        question3.getSelectionList().add(selection3);
        question3.getSelectionList().add(selection4);
        return surveyDTO;
    }


    private static QuestionDTO.SelectionDTO buildSelectionDTO(String body, boolean isEtc) {
        return QuestionDTO.SelectionDTO.builder()
                .body(body)
                .isEtc(isEtc)
                .build();
    }

    private static QuestionDTO buildQuestionDTO(String body, QuestionType questionType) {
        return QuestionDTO.builder()
                .body(body)
                .questionType(questionType)
                .selectionList(new ArrayList<>())
                .build();
    }

    private static SurveyWithDetailDTO buildSurveyWithDetailDTO(String userId) {
        return SurveyWithDetailDTO.builder()
                .title("survey3")
                .description("description3")
                .createDate(LocalDateTime.now())
                .expireDate(LocalDateTime.now().plusDays(30))
                .creatorId(userId)
                .questionList(new ArrayList<>())
                .build();
    }

}
