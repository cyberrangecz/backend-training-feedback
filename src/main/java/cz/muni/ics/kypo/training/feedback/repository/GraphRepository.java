package cz.muni.ics.kypo.training.feedback.repository;

import cz.muni.ics.kypo.training.feedback.model.Graph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GraphRepository extends JpaRepository<Graph, Long> {

    Graph findByTraineeSandboxId(Long sandboxId);

    List<Graph> findByLabel(String label);
}
