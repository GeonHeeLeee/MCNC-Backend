package mcnc.survwey.api.survey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.UserCreatedSurveyDTO;
import mcnc.survwey.domain.survey.SurveyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyInquiryService {

    private final SurveyRepository surveyRepository;

    public Page<UserCreatedSurveyDTO> getUserCreatedSurveyList(String userId, Pageable pageable) {
        Page<Object[]> surveyPageList = surveyRepository.findSurveyListWithRespondCountByUserId(userId, pageable);
        Page<UserCreatedSurveyDTO> surveyDTOPage = surveyPageList.map(record -> new UserCreatedSurveyDTO(
                (Long) record[0],
                (String) record[1],
                (String) record[2],
                ((Timestamp) record[3]).toLocalDateTime(),
                ((Timestamp) record[4]).toLocalDateTime(),
                ((Number) record[5]).intValue()
        ));

        return surveyDTOPage;
    }
}
