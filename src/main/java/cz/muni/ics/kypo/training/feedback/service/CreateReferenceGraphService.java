package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionReferenceSolution;
import cz.muni.ics.kypo.training.feedback.model.Edge;
import cz.muni.ics.kypo.training.feedback.model.Graph;
import cz.muni.ics.kypo.training.feedback.model.Node;
import cz.muni.ics.kypo.training.feedback.model.SubGraph;
import cz.muni.ics.kypo.training.feedback.constants.GraphConstants;
import cz.muni.ics.kypo.training.feedback.service.api.ElasticsearchServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateReferenceGraphService {

    private final GraphService graphService;

    public Graph createReferenceGraph(List<DefinitionLevel> definitionLevels) {
        List<String> possibleBlueNodesLabels = new ArrayList<>();
        Long visibleLevelId = GraphConstants.FIRST_LEVEL_ID;
        boolean initStartNode = true;
        Graph referenceGraph = Graph.builder()
                .label(GraphConstants.REFERENCE_GRAPH_LABEL)
                .build();

        for (DefinitionLevel level : definitionLevels) {
            SubGraph subgraph = SubGraph.builder()
                    .label("Level: " + visibleLevelId.toString())
                    .graph(referenceGraph)
                    .build();
            visibleLevelId++;
            if (initStartNode) {
                subgraph.getNodes().add(Node.builder()
                        .color(GraphConstants.GREEN)
                        .label(GraphConstants.START_NODE_LABEL)
                        .subGraph(subgraph)
                        .build());
                DefinitionReferenceSolution firstSolution = level.getDefinitionReferenceSolutions().get(0);
                List<String> commandList = Arrays.asList(firstSolution.getCmd().split("\\s"));
                subgraph.getEdges().add(Edge.builder()
                        .type(firstSolution.getCmdType())
                        .tool(commandList.get(0))
                        .options(Collections.singletonList(String.join(", ", commandList.subList(1, commandList.size()))))
                        .subGraph(subgraph)
                        .fromNode(GraphConstants.START_NODE_LABEL)
                        .toNode(level.getDefinitionReferenceSolutions().get(0).getStateName())
                        .build());
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
                                .options(Collections.singletonList(String.join(", ", commandList.subList(1, commandList.size()))))
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
        return graphService.create(referenceGraph);
    }
}
