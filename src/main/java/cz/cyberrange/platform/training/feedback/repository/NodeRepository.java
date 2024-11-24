package cz.cyberrange.platform.training.feedback.repository;

import cz.cyberrange.platform.training.feedback.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<Node, Long> {
}
