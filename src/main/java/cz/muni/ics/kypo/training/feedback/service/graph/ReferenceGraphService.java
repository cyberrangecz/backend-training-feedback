package cz.muni.ics.kypo.training.feedback.service.graph;

import cz.muni.ics.kypo.training.feedback.annotations.transactions.TransactionalWO;
import cz.muni.ics.kypo.training.feedback.constants.GraphConstants;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionReferenceSolution;
import cz.muni.ics.kypo.training.feedback.enums.GraphType;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityErrorDetail;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityNotFoundException;
import cz.muni.ics.kypo.training.feedback.model.Edge;
import cz.muni.ics.kypo.training.feedback.model.Graph;
import cz.muni.ics.kypo.training.feedback.model.Node;
import cz.muni.ics.kypo.training.feedback.model.SubGraph;
import cz.muni.ics.kypo.training.feedback.repository.GraphRepository;
import cz.muni.ics.kypo.training.feedback.service.CRUDServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferenceGraphService extends CRUDServiceImpl<Graph, Long> {

    private final GraphRepository graphRepository;

    @Override
    public JpaRepository<Graph, Long> getDAO() {
        return graphRepository;
    }

    public Graph createReferenceGraph(Long definitionId, List<DefinitionLevel> definitionLevels) {
        List<String> possibleBlueNodesLabels = new ArrayList<>();
        boolean initStartNode = true;
        Graph referenceGraph = Graph.builder()
                .label(GraphConstants.REFERENCE_GRAPH_LABEL)
                .graphType(GraphType.REFERENCE_GRAPH)
                .trainingDefinitionId(definitionId)
                .build();

        for (DefinitionLevel level : definitionLevels) {
            SubGraph subgraph = SubGraph.builder()
                    .label("Level: " + level.getLevelOrder())
                    .graph(referenceGraph)
                    .build();
            if (initStartNode) {
                subgraph.getNodes().add(Node.builder()
                        .color(GraphConstants.GREEN)
                        .label(GraphConstants.START_NODE_LABEL)
                        .subGraph(subgraph)
                        .build());
                if(!level.getDefinitionReferenceSolutions().isEmpty()) {
                    DefinitionReferenceSolution firstSolution = level.getDefinitionReferenceSolutions().get(0);
                    List<String> commandList = Arrays.asList(firstSolution.getCmd().split("\\s"));
                    subgraph.getEdges().add(Edge.builder()
                            .type(firstSolution.getCmdType())
                            .tool(commandList.get(0))
                            .options(Collections.singleton(String.join(", ", commandList.subList(1, commandList.size()))))
                            .subGraph(subgraph)
                            .fromNode(GraphConstants.START_NODE_LABEL)
                            .toNode(level.getDefinitionReferenceSolutions().get(0).getStateName())
                            .build());
                }
                initStartNode = false;
            }
            level.getDefinitionReferenceSolutions().forEach(s -> {
                subgraph.getNodes().add(Node.builder()
                        .color(s.isOptional() ? GraphConstants.GRAY : GraphConstants.GREEN)
                        .label(s.getStateName())
                        .subGraph(subgraph)
                        .build());

                if (s.getPrereqState().size() > 1) {
                    possibleBlueNodesLabels.addAll(s.getPrereqState());
                }
                List<String> commandList = Arrays.asList(s.getCmd().split("\\s"));
                s.getPrereqState().forEach(prereqState ->
                        subgraph.getEdges().add(Edge.builder()
                                .type(s.getCmdType())
                                .tool(commandList.get(0))
                                .options(Collections.singleton(String.join(", ", commandList.subList(1, commandList.size()))))
                                .fromNode(prereqState)
                                .toNode(s.getStateName())
                                .subGraph(subgraph)
                                .build())
                );
            });
            //create LIGHT_BLUE nodes
            subgraph.getNodes().forEach(node -> {
                if (possibleBlueNodesLabels.contains(node.getLabel()) && node.getColor().equals(GraphConstants.GRAY)) {
                    node.setColor(GraphConstants.LIGHT_BLUE);
                }
            });
            //add backward edge from GRAY nodes
            subgraph.getNodes().forEach(node -> {
                if (node.getColor().equals(GraphConstants.GRAY) &&
                        subgraph.getEdges().stream().noneMatch(e -> e.getFromNode().equals(node.getLabel()))) {
                    subgraph.getEdges().add(Edge.builder()
                            .fromNode(node.getLabel())
                            .tool("")
                            .type("")
                            .toNode(subgraph.getEdges().stream().
                                    filter(edge -> edge.getToNode().equals(node.getLabel()))
                                    .map(Edge::getFromNode)
                                    .collect(Collectors.toList())
                                    .get(0))
                            .subGraph(subgraph)
                            .build());
                }
            });
            referenceGraph.getSubGraphs().add(subgraph);
        }
        return this.create(referenceGraph);
    }

    public Graph getReferenceGraph(Long definitionId) {
        Graph graph = graphRepository.findReferenceGraph(definitionId);
        if (graph == null) {
            throw new EntityNotFoundException(new EntityErrorDetail(
                    Graph.class, "Reference graph for training definition with id: " + definitionId.toString() + " not found.")
            );
        }
        return graph;
    }

    public void deleteReferenceGraph(Long definitionId) {
        this.graphRepository.deleteByTrainingDefinitionIdAndGraphType(definitionId, GraphType.REFERENCE_GRAPH);
    }

    public boolean existsReferenceGraph(Long definitionId) {
       return this.graphRepository.existsByTrainingDefinitionIdAndGraphType(definitionId, GraphType.REFERENCE_GRAPH);
    }
}
