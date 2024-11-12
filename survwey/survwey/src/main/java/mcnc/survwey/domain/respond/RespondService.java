package mcnc.survwey.domain.respond;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.user.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RespondService {

    private final RespondRepository respondRepository;
}
