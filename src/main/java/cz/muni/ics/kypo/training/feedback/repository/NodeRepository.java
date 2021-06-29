package cz.muni.ics.kypo.training.feedback.repository;

import cz.muni.ics.kypo.training.feedback.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<Node, Long> {
}
