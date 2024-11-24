package cz.cyberrange.platform.training.feedback.facade;

import cz.cyberrange.platform.training.feedback.dto.provider.GraphDTO;
import cz.cyberrange.platform.training.feedback.dto.resolver.DefinitionLevel;
import cz.cyberrange.platform.training.feedback.exceptions.EntityErrorDetail;
import cz.cyberrange.platform.training.feedback.exceptions.EntityNotFoundException;
import cz.cyberrange.platform.training.feedback.mapping.GraphMapper;
import cz.cyberrange.platform.training.feedback.model.Graph;
import cz.cyberrange.platform.training.feedback.service.graph.ReferenceGraphService;
import cz.cyberrange.platform.training.feedback.service.graph.SummaryGraphService;
import cz.cyberrange.platform.training.feedback.service.graph.TraineeGraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class GraphFacade {

    private final GraphMapper graphMapper;
    private final TraineeGraphService traineeGraphService;
    private final ReferenceGraphService referenceGraphService;
    private final SummaryGraphService summaryGraphService;

    public GraphDTO getReferenceGraph(Long definitionId) {
        return graphMapper.mapGraphToGraphDTO(referenceGraphService.getReferenceGraph(definitionId));
    }

    public GraphDTO getSummaryGraph(Long instanceId) {
        return graphMapper.mapGraphToGraphDTO(summaryGraphService.getSummaryGraph(instanceId));
    }

    public GraphDTO getTraineeGraph(Long runId) {
        return graphMapper.mapGraphToGraphDTO(traineeGraphService.getTraineeGraph(runId));
    }

    public GraphDTO createReferenceGraph(Long definitionId, List<DefinitionLevel> definitionLevels) {
        return graphMapper.mapGraphToGraphDTO(referenceGraphService.createReferenceGraph(definitionId, definitionLevels));
    }

    public GraphDTO createSummaryGraph(Long definitionId, Long instanceId) {
        return graphMapper.mapGraphToGraphDTO(summaryGraphService.createSummaryGraph(definitionId, instanceId));
    }

    public void createTraineeGraphLocalInstance(Long definitionId, Long instanceId, Long runId, List<DefinitionLevel> definitionLevels, String accessToken) {
        traineeGraphService.createTraineeGraphLocalInstance(definitionId, instanceId, runId, definitionLevels, accessToken);
    }

    public void createTraineeGraphCloudInstance(Long definitionId, Long instanceId, Long runId, List<DefinitionLevel> definitionLevels) {
        traineeGraphService.createTraineeGraphCloudInstance(definitionId, instanceId, runId, definitionLevels);
    }

    public void deleteReferenceGraph(Long definitionId) {
        if (!referenceGraphService.existsReferenceGraph(definitionId)) {
            throw new EntityNotFoundException(new EntityErrorDetail(Graph.class, "Reference graph for definition with id: " + definitionId + " not found."));
        }
        referenceGraphService.deleteReferenceGraph(definitionId);
    }

    public void deleteSummaryGraph(Long instanceId) {
        if (!summaryGraphService.existsSummaryGraph(instanceId)) {
            throw new EntityNotFoundException(new EntityErrorDetail(Graph.class, "Summary graph for instance with id: " + instanceId + " not found."));
        }
        summaryGraphService.deleteSummaryGraph(instanceId);
    }

    public void deleteTraineeGraph(Long runId) {
        if (!traineeGraphService.existsTraineeGraph(runId)) {
            throw new EntityNotFoundException(new EntityErrorDetail(Graph.class, "Trainee graph for run with id: " + runId + " not found."));
        }
        traineeGraphService.deleteTraineeGraph(runId);
    }

    public void deleteAllByTrainingInstance(Long instanceId) {
        summaryGraphService.deleteSummaryGraph(instanceId);
        traineeGraphService.deleteTraineeGraphsByTrainingInstance(instanceId);
    }
}
