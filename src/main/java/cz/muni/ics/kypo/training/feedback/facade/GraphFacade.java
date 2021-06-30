package cz.muni.ics.kypo.training.feedback.facade;

import cz.muni.ics.kypo.training.feedback.dto.provider.GraphDTO;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.mapping.GraphMapper;
import cz.muni.ics.kypo.training.feedback.service.CreateReferenceGraphService;
import cz.muni.ics.kypo.training.feedback.service.CreateSummaryGraphService;
import cz.muni.ics.kypo.training.feedback.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GraphFacade {

    private final GraphMapper graphMapper;
    private final GraphService graphService;
    private final CreateReferenceGraphService createReferenceGraphService;
    private final CreateSummaryGraphService createSummaryGraphService;

    public GraphDTO getGraph(Long sandboxId) {
        return graphMapper.mapGraphToGraphDTO(graphService.getGraphByTraineeSandboxId(sandboxId));
    }

    public GraphDTO getGraph(String label) {
        return graphMapper.mapGraphToGraphDTO(graphService.getGraphByLabel(label));

    }

    public GraphDTO createReferenceGraph(List<DefinitionLevel> definitionLevels) {
        return graphMapper.mapGraphToGraphDTO(createReferenceGraphService.createReferenceGraph(definitionLevels));
    }

    public GraphDTO createSummaryGraph() {
        return graphMapper.mapGraphToGraphDTO(createSummaryGraphService.createSummaryGraph());
    }
}
