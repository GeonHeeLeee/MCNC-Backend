package mcnc.survwey.survey;



import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.inquiry.service.SurveyInquiryService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.util.BaseIntegrationTest;
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

        // When
        Page<SurveyWithCountDTO> result = surveyInquiryService.getUserCreatedSurveyList(
                "testUser1", PAGE_NUMBER, PAGE_SIZE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("사용자가 만든 설문 조회 실패 테스트 - 사용자가 만든 설문 없음")
    public void testFailedGetUserCreatedSurveyList() {
        //given

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
        Survey survey = surveyRepository.findById(1L).get();

        // When
        SurveyWithDetailDTO surveyWithDetail = surveyInquiryService.getSurveyWithDetail(survey.getSurveyId());

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
        assertThrows(CustomException.class, () -> surveyInquiryService.getSurveyWithDetail(notExistingSurveyId));
    }


//    @Test
//    @DisplayName("응답한 설문 리스트 조회 성공 테스트")
//    public void testSuccessGetUserRespondSurveyList() {
//        //Given
//
//        //When
//
//
//    }


    @Test
    @DisplayName("본인이 만든 설문 조사 테스트 - 본인이 생성한 설문")
    public void testIsSurveyUserMade_True() {
        //Given
        Long survey1Id = 1L;
        String userId = "testUser1";

        //When
        Map<String, Boolean> result = surveyInquiryService.isSurveyUserMade(userId, survey1Id);

        //Then
        assertNotNull(result.get("result"));
        assertEquals(result.get("result"), true);
    }

    @Test
    @DisplayName("본인이 만든 설문 조사 테스트 - 본인이 생성하지 않은 설문")
    public void testIsSurveyUserMade_False() {
        //Given
        Long survey1Id = 1L;
        String userId = "testUser2";

        //When
        Map<String, Boolean> result = surveyInquiryService.isSurveyUserMade(userId, survey1Id);

        //Then
        assertNotNull(result.get("result"));
        assertEquals(result.get("result"), false);
    }

}