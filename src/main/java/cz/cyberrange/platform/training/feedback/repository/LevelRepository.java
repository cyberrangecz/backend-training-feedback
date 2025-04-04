package cz.cyberrange.platform.training.feedback.repository;

import cz.cyberrange.platform.training.feedback.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LevelRepository extends JpaRepository<Level, Long> {

    List<Level> findByTraineeSandboxId(Long sandboxId);
}