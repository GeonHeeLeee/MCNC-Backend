package mcnc.survwey.api.survey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.UserCreatedSurveyDTO;
import mcnc.survwey.domain.survey.SurveyRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyInquiryService {

    private final SurveyRepository surveyRepository;

    public List<UserCreatedSurveyDTO> getUserCreatedSurveyList(String userId) {
        return surveyRepository.findSurveyListWithRespondCountByUserId(userId)
                .stream().map(record -> new UserCreatedSurveyDTO(
                        (Long) record[0],
                        (String) record[1],
                        (String) record[2],
                        ((java.sql.Timestamp) record[3]).toLocalDateTime(),
                        ((java.sql.Timestamp) record[4]).toLocalDateTime(),
                        ((Number) record[5]).intValue()
                )).toList();
    }
}
