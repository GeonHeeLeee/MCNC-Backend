package mcnc.survwey.api.survey.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.SearchDTO;
import mcnc.survwey.api.survey.dto.SurveyDTO;
import mcnc.survwey.api.survey.dto.SurveyInfoDTO;
import mcnc.survwey.api.survey.service.SurveySearchService;
import mcnc.survwey.domain.survey.Survey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("survey/")
public class SurveySearchController {

    private final SurveySearchService surveySearchService;

    @PostMapping("/search")
    public ResponseEntity<Object> surveySearch(@RequestBody SearchDTO searchDTO){
        log.info("Received title: {}", searchDTO.getTitle());

        List<Survey> surveys = surveySearchService.surveySearch(searchDTO);
        List<SurveyInfoDTO> surveyDTOs = surveys.stream().map(survey -> {
            SurveyInfoDTO dto = SurveyInfoDTO.builder()
                    .surveyId(survey.getSurveyId())
                    .title(survey.getTitle())
                    .username(survey.getUser().getName())
                    .build();
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(surveyDTOs);
    }
}
