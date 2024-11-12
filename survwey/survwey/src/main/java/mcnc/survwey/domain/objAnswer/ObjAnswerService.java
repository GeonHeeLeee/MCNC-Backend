package mcnc.survwey.domain.objAnswer;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.user.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObjAnswerService {
    private final ObjAnswerRepository objAnswerRepository;
}
