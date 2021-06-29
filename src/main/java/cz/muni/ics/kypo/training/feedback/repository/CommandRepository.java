package cz.muni.ics.kypo.training.feedback.repository;

import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
    List<Command> findByLevelIdAndLevelTraineeSandboxId(Long levelId, Long sandboxId);

    List<Command> findByLevelTraineeSandboxId(Long sandboxId);

    List<Command> findByLevelTraineeSandboxIdAndMistakeMistakeType(Long sandboxId, MistakeType mistakeType);

    List<Command> findByLevelTraineeSandboxIdAndMistakeIsNotNull(Long sandboxId);

    List<Command> findByMistakeIsNotNull();

    List<Command> findByMistakeIsNull();

    List<Command> findByMistakeMistakeType(MistakeType mistakeType);

}
