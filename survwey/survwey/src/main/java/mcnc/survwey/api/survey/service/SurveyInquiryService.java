package mcnc.survwey.api.survey.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.UserCreatedSurveyDTO;
import mcnc.survwey.domain.survey.SurveyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyInquiryService {

    private final SurveyRepository surveyRepository;

    public Map<String, List<UserCreatedSurveyDTO>> getUserCreatedSurveyList(String userId) {
        List<Object[]> rawData = surveyRepository.findSurveyListWithRespondCountByUserId(userId);
        LocalDateTime currentTime = LocalDateTime.now();

        Map<String, List<UserCreatedSurveyDTO>> surveyMap = new HashMap<>();
        surveyMap.put("expiredSurvey", new ArrayList<>());
        surveyMap.put("ongoingSurvey", new ArrayList<>());

        rawData.stream().map(record -> new UserCreatedSurveyDTO(
                        (Long) record[0],
                        (String) record[1],
                        (String) record[2],
                        ((java.sql.Timestamp) record[3]).toLocalDateTime(),
                        ((java.sql.Timestamp) record[4]).toLocalDateTime(),
                        ((Number) record[5]).intValue()
                ))
                .forEach(surveyDTO -> {
                    LocalDateTime expireDate = surveyDTO.getExpireDate();
                    if (expireDate.isAfter(currentTime)) {
                        surveyMap.get("ongoingSurvey").add(surveyDTO);
                    } else {
                        surveyMap.get("expiredSurvey").add(surveyDTO);
                    }
                });

        return surveyMap;
    }
}
