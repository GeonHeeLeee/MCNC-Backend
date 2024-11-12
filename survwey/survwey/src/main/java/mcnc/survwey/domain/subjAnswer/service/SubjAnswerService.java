package mcnc.survwey.domain.subjAnswer.service;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.subjAnswer.repository.SubjAnswerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjAnswerService {
    private final SubjAnswerRepository subjAnswerRepository;

}
