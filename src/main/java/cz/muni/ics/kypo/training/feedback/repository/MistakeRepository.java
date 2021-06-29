package cz.muni.ics.kypo.training.feedback.repository;

import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.model.Mistake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MistakeRepository extends JpaRepository<Mistake, Long> {

    Optional<Mistake> findMistakeByMistakeType(MistakeType mistakeType);
}
