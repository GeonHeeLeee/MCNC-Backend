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
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SelectionService {
    private final SelectionRepository selectionRepository;

    /**
     * 보기 생성
     * @param createdQuestion
     * @param selectionDTOList
     * @Author 이건희
     */
    public void buildAndSaveSelection(Question createdQuestion, List<QuestionDTO.SelectionDTO> selectionDTOList) {
        List<Selection> selectionList = IntStream.range(0, selectionDTOList.size())
                .mapToObj(sequence -> {
                    QuestionDTO.SelectionDTO selectionDTO = selectionDTOList.get(sequence);
                    SelectionId selectionId = new SelectionId(createdQuestion.getQuesId(), sequence); // 인덱스 사용
                    Selection selection = selectionDTO.toEntity(selectionId, createdQuestion);
                    createdQuestion.addSelection(selection);
                    return selection;
                }).toList();

        selectionRepository.saveAll(selectionList);
    }

    /**
     * 보기 찾기
     * @param selectionId
     * @return
     * @Author 이건희
     */
    public Selection findBySelectionId(SelectionId selectionId) {
        return selectionRepository.findById(selectionId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SELECTION_NOT_FOUND_BY_ID));
    }
}
