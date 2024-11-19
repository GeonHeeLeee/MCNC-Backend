package mcnc.survwey.domain.subjAnswer.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.service.QuestionService;
import mcnc.survwey.domain.respond.dto.ResponseDTO;
import mcnc.survwey.domain.subjAnswer.SubjAnswer;
import mcnc.survwey.domain.subjAnswer.repository.SubjAnswerRepository;
import mcnc.survwey.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjAnswerService {

    private final QuestionService questionService;

    public List<SubjAnswer> createSubjectiveAnswers(List<ResponseDTO> responseList, User respondedUser) {
        return responseList.stream()
                .filter(responseDTO -> responseDTO.getQuestionType() == QuestionType.SUBJECTIVE)
                .map(responseDTO -> {
                    Question question = questionService.findByQuesId(responseDTO.getQuesId());
                    return SubjAnswer.create(respondedUser, responseDTO.getResponse(), question);
                })
                .collect(Collectors.toList());
    }
}
