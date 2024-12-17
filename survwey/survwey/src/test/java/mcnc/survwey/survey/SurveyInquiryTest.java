package mcnc.survwey.survey;



import mcnc.survwey.api.survey.inquiry.dto.SurveyWithDateDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
import mcnc.survwey.api.survey.manage.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.repository.SurveyRepository;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.api.survey.inquiry.service.SurveyInquiryService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.util.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional //테스트 후 롤백
public class SurveyInquiryTest extends BaseIntegrationTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyInquiryService surveyInquiryService;

    @Test
    @DisplayName("사용자가 만든 설문 조회 성공 테스트")
    public void testSuccessfulGetUserCreatedSurveyList() {
        //given
        String userId = "testUser1";

        // When
        Page<SurveyWithCountDTO> result = surveyInquiryService.getUserCreatedSurveyList(
                userId, PAGE_NUMBER, PAGE_SIZE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("사용자가 만든 설문 조회 실패 테스트 - 사용자가 만든 설문 없음")
    public void testFailedGetUserCreatedSurveyList() {
        //given
        String userId = "testUser2";

        // When
        Page<SurveyWithCountDTO> result = surveyInquiryService.getUserCreatedSurveyList(
                userId, PAGE_NUMBER, PAGE_SIZE);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getNumberOfElements());
    }

    @Test
    @DisplayName("특정 설문 조회 성공 테스트")
    public void testSuccessfulGetSurveyWithDetail() {
        // Given
        Survey survey = surveyRepository.findById(1L).get();

        // When
        SurveyWithDetailDTO surveyWithDetail = surveyInquiryService.findSurveyWithDetail(survey.getSurveyId());

        // Then
        assertEquals(surveyWithDetail.getSurveyId(), survey.getSurveyId());
        assertEquals(surveyWithDetail.getQuestionList().size(), 3);
        assertEquals(surveyWithDetail.getQuestionList().get(1).getSelectionList().size(), 2);
    }

    @Test
    @DisplayName("특정 설문 조회 실패 테스트 - 설문 아이디 존재하지 않음")
    public void testFailedGetSurveyWithDetail() {
        // Given
        long notExistingSurveyId = Integer.MAX_VALUE;
        // When-Then
        assertThrows(CustomException.class, () -> surveyInquiryService.findSurveyWithDetail(notExistingSurveyId));
    }


    @Test
    @DisplayName("응답한 설문 리스트 조회 성공 테스트")
    public void testSuccessGetUserRespondSurveyList() {
        //Given
        String userId = "testUser1";

        //When
        Page<SurveyWithDateDTO> result = surveyInquiryService.getUserRespondSurveyList(userId, PAGE_NUMBER, PAGE_SIZE);

        //Then
        assertEquals(result.getTotalElements(), 1);
    }

}