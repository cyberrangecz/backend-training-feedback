package cz.muni.ics.kypo.training.feedback.facade;

import cz.muni.ics.kypo.training.feedback.dto.provider.LevelDTO;
import cz.muni.ics.kypo.training.feedback.mapping.LevelMapper;
import cz.muni.ics.kypo.training.feedback.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LevelFacade {

    private final LevelMapper levelMapper;
    private final LevelService levelService;

    public List<LevelDTO> getLevelsByTrainee(Long sandboxId) {
        return levelMapper.mapToListLevelDTO(levelService.getLevelsByTraineeSandboxId(sandboxId));
    }
}
