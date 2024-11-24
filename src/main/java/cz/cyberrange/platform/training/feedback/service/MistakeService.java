package cz.cyberrange.platform.training.feedback.service;

import cz.cyberrange.platform.training.feedback.enums.MistakeType;
import cz.cyberrange.platform.training.feedback.model.Mistake;
import cz.cyberrange.platform.training.feedback.repository.MistakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MistakeService extends CRUDServiceImpl<Mistake, Long> {

    private final MistakeRepository mistakeRepository;

    @Override
    public JpaRepository<Mistake, Long> getDAO() {
        return mistakeRepository;
    }

    public Optional<Mistake> getMistakeByMistakeType(MistakeType mistakeType) {
        return mistakeRepository.findMistakeByMistakeType(mistakeType);
    }
}
