package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.model.Mistake;
import cz.muni.ics.kypo.training.feedback.repository.MistakeRepository;
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
