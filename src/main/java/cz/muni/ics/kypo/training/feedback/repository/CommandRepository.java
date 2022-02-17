package cz.muni.ics.kypo.training.feedback.repository;

import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.model.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long>, CommandRepositoryCustom {
    List<Command> findByLevelIdAndLevelTraineeTrainingRunId(Long levelId, Long trainingRunId);

    List<Command> findByLevelTraineeTrainingRunIdOrderByTrainingTime(Long trainingRunId);

    List<Command> findByLevelTraineeTrainingRunIdAndMistakeMistakeType(Long trainingRunId, MistakeType mistakeType);

    List<Command> findByLevelTraineeTrainingRunIdAndMistakeIsNotNull(Long trainingRunId);

    List<Command> findByMistakeIsNotNull();

    List<Command> findByMistakeIsNull();

    List<Command> findByLevelTraineeTrainingRunIdInAndMistakeIsNull(List<Long> trainingRunId);

    List<Command> findByMistakeMistakeType(MistakeType mistakeType);

}
