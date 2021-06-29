package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.model.Level;
import cz.muni.ics.kypo.training.feedback.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional
public class LevelService extends CRUDServiceImpl<Level, Long> {

    private final LevelRepository levelRepository;

    @Override
    public JpaRepository<Level, Long> getDAO() {
        return levelRepository;
    }

    public List<Level> getLevelsByTraineeSandboxId(Long sandboxId) {
        List<Level> levels = levelRepository.findByTraineeSandboxId(sandboxId);
        if (levels == null || levels.isEmpty()) {
            throw new NoSuchElementException("There does not exist levels for trainee with sandboxId :" + sandboxId);
        }
        return levels;
    }
}
