package mcnc.survwey.domain.subjAnswer.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.question.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.service.QuestionService;
import mcnc.survwey.api.survey.response.dto.reply.ReplyDTO;
import mcnc.survwey.domain.subjAnswer.SubjAnswer;
import mcnc.survwey.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjAnswerService {

    private final QuestionService questionService;

    /**
     * 주관식 응답 추가
     * - 질문 타입이 주관식인 것들만 필터링
     * - 객체 생성
     * @param responseList
     * @param respondedUser
     * @return
     */
    public List<SubjAnswer> createSubjectiveAnswers(List<ReplyDTO> responseList, User respondedUser) {
        return responseList.stream()
                .filter(replyDTO -> replyDTO.getQuestionType() == QuestionType.SUBJECTIVE)
                .map(replyDTO -> {
                    Question question = questionService.findByQuesId(replyDTO.getQuesId());
                    return SubjAnswer.create(respondedUser, replyDTO.getResponse(), question);
                })
                .collect(Collectors.toList());
    }
}
