package mcnc.survwey.domain.selection.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.survey.common.dto.SelectionDTO;
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

    public void buildAndSaveSelection(Question createdQuestion, List<SelectionDTO> selectionDTOList) {
        List<Selection> selectionList = selectionDTOList.stream()
                .map(selectionDTO -> {
                    SelectionId selectionId = new SelectionId(createdQuestion.getQuesId(), selectionDTOList.indexOf(selectionDTO));
                    return selectionDTO.toEntity(selectionId, createdQuestion);
                })
                .toList();
        selectionRepository.saveAll(selectionList);
    }


    public Selection findBySelectionId(SelectionId selectionId) {
        return selectionRepository.findById(selectionId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SELECTION_NOT_FOUND_BY_ID));
    }
}
