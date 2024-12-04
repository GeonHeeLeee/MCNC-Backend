package mcnc.survwey.domain.selection.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.manage.dto.QuestionDTO;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.selection.SelectionId;
import mcnc.survwey.domain.selection.repository.SelectionRepository;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SelectionService {
    private final SelectionRepository selectionRepository;

    public void buildAndSaveSelection(Question createdQuestion, List<QuestionDTO.SelectionDTO> selectionDTO1List) {
        List<Selection> selectionList = selectionDTO1List.stream()
                .map(selectionDTO -> {
                    SelectionId selectionId = new SelectionId(createdQuestion.getQuesId(), selectionDTO1List.indexOf(selectionDTO));
                    Selection selection = selectionDTO.toEntity(selectionId, createdQuestion);
                    createdQuestion.addSelection(selection);
                    return selection;
                })
                .toList();
        selectionRepository.saveAll(selectionList);
    }


    public Selection findBySelectionId(SelectionId selectionId) {
        return selectionRepository.findById(selectionId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SELECTION_NOT_FOUND_BY_ID));
    }
}
