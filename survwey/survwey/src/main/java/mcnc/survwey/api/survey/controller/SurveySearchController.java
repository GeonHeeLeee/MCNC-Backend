package mcnc.survwey.api.survey.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.dto.SearchDTO;
import mcnc.survwey.api.survey.dto.SurveyDTO;
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
        List<SurveyDTO> surveyDTOs = surveys.stream().map(survey -> {
            SurveyDTO dto = new SurveyDTO();
            dto.setSurveyId(survey.getSurveyId());
            dto.setTitle(survey.getTitle());
            dto.setCreateDate(survey.getCreateDate());
            dto.setExpireDate(survey.getExpireDate());
            dto.setDescription(survey.getDescription());

            // 만약 User 정보를 포함하고 싶다면, User 정보만 가져오거나 DTO로 변환
            if (survey.getUser() != null) {
                dto.setUsername(survey.getUser().getName());
            }

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(surveyDTOs);
    }
}
