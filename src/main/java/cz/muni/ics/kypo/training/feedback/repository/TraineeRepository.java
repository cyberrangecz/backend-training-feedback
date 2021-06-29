package cz.muni.ics.kypo.training.feedback.repository;

import cz.muni.ics.kypo.training.feedback.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findTraineeBySandboxId(Long sandboxId);
}
