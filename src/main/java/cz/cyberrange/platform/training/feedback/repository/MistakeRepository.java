package cz.cyberrange.platform.training.feedback.repository;

import cz.cyberrange.platform.training.feedback.enums.MistakeType;
import cz.cyberrange.platform.training.feedback.model.Mistake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MistakeRepository extends JpaRepository<Mistake, Long> {

    Optional<Mistake> findMistakeByMistakeType(MistakeType mistakeType);
}
