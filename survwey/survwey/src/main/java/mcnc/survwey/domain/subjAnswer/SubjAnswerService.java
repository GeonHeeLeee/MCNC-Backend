package mcnc.survwey.domain.subjAnswer;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.dto.ResponseDTO;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.user.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjAnswerService {
    private final SubjAnswerRepository subjAnswerRepository;

}
