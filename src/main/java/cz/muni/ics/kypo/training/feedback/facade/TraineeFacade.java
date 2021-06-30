package cz.muni.ics.kypo.training.feedback.facade;

import cz.muni.ics.kypo.training.feedback.dto.provider.TraineeDTO;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.mapping.TraineeMapper;
import cz.muni.ics.kypo.training.feedback.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TraineeFacade {

    private final TraineeMapper traineeMapper;
    private final TraineeService traineeService;

    public List<TraineeDTO> getTrainees() {
        return traineeMapper.mapToListTraineeDTO(traineeService.getAll());
    }

    public TraineeDTO getTraineeBySandboxId(Long sandboxId) {
        return traineeMapper.mapTraineeToTraineeDTO(traineeService.getTraineeBySandboxId(sandboxId));
    }

    public void createTraineeBySandboxId(Long definitionId, Long instanceId, Long sandboxId, List<DefinitionLevel> definitionLevels) {
        traineeService.create(definitionId, instanceId, sandboxId, definitionLevels);
    }
}
