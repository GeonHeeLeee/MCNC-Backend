package mcnc.survwey.domain.objAnswer.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.objAnswer.repository.ObjAnswerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObjAnswerService {
    private final ObjAnswerRepository objAnswerRepository;
}
