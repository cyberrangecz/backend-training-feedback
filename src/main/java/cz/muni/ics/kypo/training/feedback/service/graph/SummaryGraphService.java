package cz.muni.ics.kypo.training.feedback.service.graph;

import cz.muni.ics.kypo.training.feedback.annotations.transactions.TransactionalWO;
import cz.muni.ics.kypo.training.feedback.constants.GraphConstants;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SummaryGraphService extends CRUDServiceImpl<Graph, Long> {

    private final GraphRepository graphRepository;

    @Override
    public JpaRepository<Graph, Long> getDAO() {
        return graphRepository;
    }

    public Graph getSummaryGraph(Long instanceId) {
        Graph graph = graphRepository.findSummaryGraph(instanceId);
        if (graph == null) {
            throw new EntityNotFoundException(new EntityErrorDetail(
                    Graph.class, "Summary graph for training instance with id: " + instanceId.toString() + " not found.")
            );
        }
        return graph;
    }

    public Graph createSummaryGraph(Long definitionId, Long instanceId) {
        List<Graph> traineeGraphs = graphRepository.getAllByTrainingDefinitionIdAndTrainingInstanceId(definitionId, instanceId)
                .stream()
                .filter(g -> g.getTrainee() != null)
                .collect(Collectors.toList());
        if(traineeGraphs.isEmpty()) {
            return null;
        }
        Graph summaryGraph = initSummaryGraph(definitionId, instanceId);
        SubGraph previousSummarySubgraph = null;

        for (SubGraph summarySubgraph : summaryGraph.getSubGraphs()) {
            List<SubGraph> traineesSubGraphs = traineeGraphs.stream()
                    .flatMap(g -> g.getSubGraphs().stream().filter(sg -> sg.getLabel().equals(summarySubgraph.getLabel())))
                    .collect(Collectors.toList());
            createRedNodesAndEdgesForSummarySubgraph(summarySubgraph, traineesSubGraphs);
            if (previousSummarySubgraph != null) {
                processStartNodesRedEdges(previousSummarySubgraph, traineesSubGraphs);
            }
            previousSummarySubgraph = summarySubgraph;
        }
        return this.create(summaryGraph);
    }

    public boolean existsSummaryGraph(Long instanceId) {
        return this.graphRepository.existsByTrainingInstanceIdAndGraphType(instanceId, GraphType.SUMMARY_GRAPH);
    }

    public void deleteSummaryGraph(Long instanceId) {
        this.graphRepository.deleteByTrainingInstanceIdAndGraphType(instanceId, GraphType.SUMMARY_GRAPH);
    }

    private void processStartNodesRedEdges(SubGraph previousSummarySubgraph, List<SubGraph> traineesSubgraphs) {
        for (SubGraph traineeSubgraph : traineesSubgraphs) {
            String startNodeLabel = traineeSubgraph.getNodes().stream().filter(n -> n.getShape().equals(GraphConstants.DIAMOND)).map(Node::getLabel).findFirst().get();
            String fromNodeLabel = traineeSubgraph.getEdges().stream().filter(e -> e.getToNode().equals(startNodeLabel)).map(Edge::getFromNode).findFirst().get();
            List<Edge> redEdges = traineeSubgraph.getEdges().stream().filter(e -> e.getFromNode().equals(startNodeLabel)).collect(Collectors.toList());
            if (!redEdges.isEmpty()) {
                previousSummarySubgraph.getNodes().add(Node.builder()
                        .label("By: " + traineeSubgraph.getGraph().getTrainee().getTrainingRunId())
                        .name(fromNodeLabel + "By: " + traineeSubgraph.getGraph().getTrainee().getTrainingRunId())
                        .subGraph(previousSummarySubgraph)
                        .color(GraphConstants.RED)
                        .build());
                for (Edge redEdge : redEdges) {
                    previousSummarySubgraph.getEdges().add(Edge.builder()
                            .color(GraphConstants.RED)
                            .subGraph(previousSummarySubgraph)
                            .tool(redEdge.getTool())
                            .fromNode(fromNodeLabel)
                            .toNode(fromNodeLabel + "By: " + traineeSubgraph.getGraph().getTrainee().getTrainingRunId())
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
                    .map(g -> g.getGraph().getTrainee().getTrainingRunId())
                    .collect(Collectors.toList());

            for (SubGraph traineeSubgraph : traineesSubgraphs) {
                List<String> incorrectEdgesFromNode = traineeSubgraph.getEdges().stream()
                        .filter(e -> e.getFromNode().equals(summaryGraphNode.getLabel()))
                        .filter(e -> e.getColor().equals(GraphConstants.RED))
                        .map(Edge::getTool)
                        .collect(Collectors.toList());
                if (!incorrectEdgesFromNode.isEmpty()) {
                    Long trainingRunId = traineeSubgraph.getGraph().getTrainee().getTrainingRunId();
                    newRedNodes.add(Node.builder()
                            .color(GraphConstants.RED)
                            .subGraph(summarySubgraph)
                            .name(summaryGraphNode.getLabel() + "By: " + trainingRunId)
                            .label("By: " + trainingRunId)
                            .build());
                    incorrectEdgesFromNode.forEach(cmd -> newRedEdges
                            .add(Edge.builder()
                                    .toNode(summaryGraphNode.getLabel() + "By: " + trainingRunId)
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

    private Graph initSummaryGraph(Long definitionId, Long instanceId) {
        Graph referenceGraph = this.getReferenceGraph(definitionId);
        Graph summaryGraph = new Graph();
        summaryGraph.setGraphType(GraphType.SUMMARY_GRAPH);
        summaryGraph.setLabel("Summary Graph");
        summaryGraph.setTrainingDefinitionId(definitionId);
        summaryGraph.setTrainingInstanceId(instanceId);
        summaryGraph.setFontSize(referenceGraph.getFontSize());
        summaryGraph.setLabelLocation(referenceGraph.getLabelLocation());
        summaryGraph.setSubGraphs(referenceGraph.getSubGraphs().stream()
                .map(subGraph -> {
                        SubGraph clonedSubgraph = subGraph.clone();
                        clonedSubgraph.setGraph(summaryGraph);
                        return clonedSubgraph;
                }).collect(Collectors.toList())
        );
        return summaryGraph;
    }

    @Transactional
    public Graph getReferenceGraph(Long definitionId) {
        Graph graph = graphRepository.findReferenceGraph(definitionId);
        if (graph == null) {
            throw new EntityNotFoundException(new EntityErrorDetail(
                    Graph.class, "Reference graph for training definition with id: " + definitionId.toString() + " not found.")
            );
        }
        return graph;
    }
}
