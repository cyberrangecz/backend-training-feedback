package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.model.Edge;
import cz.muni.ics.kypo.training.feedback.model.Graph;
import cz.muni.ics.kypo.training.feedback.model.Node;
import cz.muni.ics.kypo.training.feedback.model.SubGraph;
import cz.muni.ics.kypo.training.feedback.constants.GraphConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateSummaryGraphService {

    private final GraphService graphService;

    public Graph createSummaryGraph() {
        List<Graph> traineeGraphs = graphService.getAll().stream().filter(g -> g.getTrainee() != null).collect(Collectors.toList());
        Graph summaryGraph = initSummaryGraph();
        SubGraph previousSummarySubgraph = null;

        for (SubGraph summarySubgraph : summaryGraph.getSubGraphs()) {
            List<SubGraph> traineesSubgraphs = traineeGraphs.stream()
                    .flatMap(g -> g.getSubGraphs().stream().filter(sg -> sg.getLabel().equals(summarySubgraph.getLabel())))
                    .collect(Collectors.toList());
            createRedNodesAndEdgesForSummarySubgraph(summarySubgraph, traineesSubgraphs);
            if (previousSummarySubgraph != null) {
                processStartNodesRedEdges(previousSummarySubgraph, traineesSubgraphs);
            }
            previousSummarySubgraph = summarySubgraph;
        }
        return graphService.create(summaryGraph);
    }

    private void processStartNodesRedEdges(SubGraph previousSummarySubgraph, List<SubGraph> traineesSubgraphs) {
        for (SubGraph traineeSubgraph : traineesSubgraphs) {
            String startNodeLabel = traineeSubgraph.getNodes().stream().filter(n -> n.getShape().equals(GraphConstants.DIAMOND)).map(Node::getLabel).findFirst().get();
            String fromNodeLabel = traineeSubgraph.getEdges().stream().filter(e -> e.getToNode().equals(startNodeLabel)).map(Edge::getFromNode).findFirst().get();
            List<Edge> redEdges = traineeSubgraph.getEdges().stream().filter(e -> e.getFromNode().equals(startNodeLabel)).collect(Collectors.toList());
            if (!redEdges.isEmpty()) {
                previousSummarySubgraph.getNodes().add(Node.builder()
                        .label("By: " + traineeSubgraph.getGraph().getTrainee().getSandboxId())
                        .name(fromNodeLabel + "By: " + traineeSubgraph.getGraph().getTrainee().getSandboxId())
                        .subGraph(previousSummarySubgraph)
                        .color(GraphConstants.RED)
                        .build());
                for (Edge redEdge : redEdges) {
                    previousSummarySubgraph.getEdges().add(Edge.builder()
                            .color(GraphConstants.RED)
                            .subGraph(previousSummarySubgraph)
                            .tool(redEdge.getTool())
                            .fromNode(fromNodeLabel)
                            .toNode(fromNodeLabel + "By: " + traineeSubgraph.getGraph().getTrainee().getSandboxId())
                            .build());
                }
            }
        }
    }

    private void createRedNodesAndEdgesForSummarySubgraph(SubGraph summarySubgraph, List<SubGraph> traineesSubgraphs) {
        List<Node> newRedNodes = new ArrayList<>();
        List<Edge> newRedEdges = new ArrayList<>();

        for (Node summaryGraphNode : summarySubgraph.getNodes()) {
            List<Long> traineesAchievedNode = traineesSubgraphs.stream()
                    .filter(g -> g.getNodes().stream()
                            .filter(n -> n.getColor().equals(GraphConstants.GREEN))
                            .map(Node::getLabel)
                            .collect(Collectors.toList())
                            .contains(summaryGraphNode.getLabel()))
                    .map(g -> g.getGraph().getTrainee().getSandboxId())
                    .collect(Collectors.toList());

            for (SubGraph traineeSubgraph : traineesSubgraphs) {
                List<String> incorrectEdgesFromNode = traineeSubgraph.getEdges().stream()
                        .filter(e -> e.getFromNode().equals(summaryGraphNode.getLabel()))
                        .filter(e -> e.getColor().equals(GraphConstants.RED))
                        .map(Edge::getTool)
                        .collect(Collectors.toList());
                if (!incorrectEdgesFromNode.isEmpty()) {
                    Long sandboxId = traineeSubgraph.getGraph().getTrainee().getSandboxId();
                    newRedNodes.add(Node.builder()
                            .color(GraphConstants.RED)
                            .subGraph(summarySubgraph)
                            .name(summaryGraphNode.getLabel() + "By: " + sandboxId)
                            .label("By: " + sandboxId)
                            .build());
                    incorrectEdgesFromNode.forEach(cmd -> newRedEdges
                            .add(Edge.builder()
                                    .toNode(summaryGraphNode.getLabel() + "By: " + sandboxId)
                                    .fromNode(summaryGraphNode.getLabel())
                                    .subGraph(summarySubgraph)
                                    .tool(cmd)
                                    .color(GraphConstants.RED)
                                    .build()));
                }
            }
            summaryGraphNode.setName(summaryGraphNode.getLabel());
            summaryGraphNode.setLabel(summaryGraphNode.getLabel() + "\n" + traineesAchievedNode);
        }
        summarySubgraph.getNodes().addAll(newRedNodes);
        summarySubgraph.getEdges().addAll(newRedEdges);
    }

    private Graph initSummaryGraph() {
        Graph summaryGraph = graphService.getGraphByLabel(GraphConstants.REFERENCE_GRAPH_LABEL);
        summaryGraph.setId(null);
        summaryGraph.setLabel("Summary Graph");
        summaryGraph.getSubGraphs().forEach(subGraph -> {
            subGraph.setId(null);
            subGraph.getNodes().forEach(node -> node.setId(null));
            subGraph.getEdges().forEach(edge -> edge.setId(null));
        });
        return summaryGraph;
    }
}
