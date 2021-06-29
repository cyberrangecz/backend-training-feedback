package cz.muni.ics.kypo.training.feedback.repository;

import cz.muni.ics.kypo.training.feedback.model.Edge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeRepository extends JpaRepository<Edge, Long> {
}
