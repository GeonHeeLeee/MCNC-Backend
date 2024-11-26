package mcnc.survwey.domain.objAnswer.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import mcnc.survwey.domain.respond.dto.ResponseDTO;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.selection.service.SelectionService;
import mcnc.survwey.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObjAnswerService {

    private final SelectionService selectionService;


    /**
     * 객관식 응답 추가
     * - 질문 타입이 객관식인 것들만 필터링
     * - 객체 생성
     * @param responseList
     * @param respondedUser
     * @return
     */
    public List<ObjAnswer> createObjectiveAnswers(List<ResponseDTO> responseList, User respondedUser) {
        return responseList.stream()
                .filter(responseDTO -> responseDTO.getQuestionType() == QuestionType.OBJ_MULTI || responseDTO.getQuestionType() == QuestionType.OBJ_SINGLE)
                .map(responseDTO -> {
                    Selection selection = selectionService.findBySelectionId(responseDTO.getSelectionId());
                    return ObjAnswer.create(respondedUser, responseDTO.getResponse(), selection);
                })
                .collect(Collectors.toList());
    }
}
