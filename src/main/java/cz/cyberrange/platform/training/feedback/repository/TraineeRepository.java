package cz.cyberrange.platform.training.feedback.repository;

import cz.cyberrange.platform.training.feedback.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findTraineeByTrainingRunId(Long trainingRunId);

    List<Trainee> findAllByTraineeGraphTrainingInstanceId(Long trainingInstanceId);
}
