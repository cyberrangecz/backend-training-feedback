package cz.muni.ics.kypo.training.feedback.service.graph;

import cz.muni.ics.kypo.training.feedback.constants.GraphConstants;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionReferenceSolution;
import cz.muni.ics.kypo.training.feedback.dto.resolver.TrainingCommand;
import cz.muni.ics.kypo.training.feedback.dto.resolver.TrainingEvent;
import cz.muni.ics.kypo.training.feedback.enums.GraphType;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityErrorDetail;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityNotFoundException;
import cz.muni.ics.kypo.training.feedback.exceptions.InternalServerErrorException;
import cz.muni.ics.kypo.training.feedback.model.*;
import cz.muni.ics.kypo.training.feedback.repository.GraphRepository;
import cz.muni.ics.kypo.training.feedback.service.CRUDServiceImpl;
import cz.muni.ics.kypo.training.feedback.service.TraineeService;
import cz.muni.ics.kypo.training.feedback.service.api.ElasticsearchServiceApi;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeGraphService extends CRUDServiceImpl<Graph, Long> {

    private final GraphRepository graphRepository;
    private final TraineeService traineeService;
    private final ElasticsearchServiceApi elasticsearchServiceApi;
    private final List<String> achievedNodesLabels = new ArrayList<>();

    @Override
    public JpaRepository<Graph, Long> getDAO() {
        return graphRepository;
    }

    public Graph getTraineeGraph(Long trainingRunId) {
        Graph graph = graphRepository.findTraineeGraph(trainingRunId);
        if (graph == null) {
            throw new EntityNotFoundException(new EntityErrorDetail(
                    Graph.class, "Trainee graph for trainee with training run id: " + trainingRunId.toString() + " not found.")
            );
        }
        return graph;
    }

    public boolean existsTraineeGraph(Long runId) {
        return this.graphRepository.existsByTraineeTrainingRunId(runId);
    }

    public void deleteTraineeGraph(Long runId) {
        this.graphRepository.deleteByTraineeTrainingRunId(runId);
    }

    public void deleteTraineeGraphsByTrainingInstance(Long instanceId) {
        this.graphRepository.deleteByTrainingInstanceIdAndGraphType(instanceId, GraphType.TRAINEE_GRAPH);
    }

    public Graph createTraineeGraph(Long definitionId, Long instanceId, Long runId,
                                    List<DefinitionLevel> definitionLevels) {
        List<TrainingEvent> events = elasticsearchServiceApi.getTrainingEventsByTrainingRunId(definitionId, instanceId, runId);
        Long sandboxId = events.get(0).getSandboxId();
        List<TrainingCommand> commands = elasticsearchServiceApi.getTrainingCommandsBySandboxId(sandboxId);

        LocalDateTime trainingStartTime = events.get(0).getTimestamp();
        commands.forEach(c -> c.setTrainingTime(Duration.between(trainingStartTime, c.getTimestamp())));

        Trainee trainee = this.traineeService.createTraineeEntity(runId, events, commands);

        Graph graph = Graph.builder()
                .trainee(trainee)
                .label(GraphConstants.TRAINEE_GRAPH_LABEL + sandboxId)
                .graphType(GraphType.TRAINEE_GRAPH)
                .trainingDefinitionId(definitionId)
                .trainingInstanceId(instanceId)
                .build();
        trainee.setTraineeGraph(graph);

        Map<Long, DefinitionLevel> definitionLevelById = definitionLevels.stream()
                .collect(Collectors.toMap(DefinitionLevel::getLevelId, Function.identity()));
        for (Level level : trainee.getLevels()) {
            Node lastAchievedNode = getLastAchievedNode(graph);
            DefinitionLevel definitionLevel = definitionLevelById.get(level.getLevelRefId());
            if (definitionLevel == null) {
                continue;
            }
            SubGraph newSubgraph = processLevelCommands(level, definitionLevel.getDefinitionReferenceSolutions(), definitionLevel.getLevelOrder(), lastAchievedNode);
            newSubgraph.setGraph(graph);
            graph.getSubGraphs().add(newSubgraph);
        }
        return graphRepository.save(graph);
    }

    private SubGraph processLevelCommands(Level level, List<DefinitionReferenceSolution> levelSolutions, Integer visibleLevelOrder, Node lastAchievedNode) {
        List<Command> commands = level.getCommands();
        SubGraph subGraph = SubGraph.builder().label("Level: " + visibleLevelOrder.toString()).build();
        Node startNode = Node.builder()
                .subGraph(subGraph)
                .color(GraphConstants.VIOLET)
                .shape(GraphConstants.DIAMOND)
                .label("level_" + visibleLevelOrder + "_start")
                .build();
        subGraph.getNodes().add(startNode);
        if (lastAchievedNode != null) {
            subGraph.getEdges().add(Edge.builder()
                    .subGraph(subGraph)
                    .toNode("level_" + visibleLevelOrder + "_start")
                    .fromNode(lastAchievedNode.getLabel())
                    .build());
        }
        lastAchievedNode = startNode;
        for (Command command : commands) {
            lastAchievedNode = processCommand(command, levelSolutions, subGraph, lastAchievedNode);
        }
        for (DefinitionReferenceSolution solution : levelSolutions) {
            List<String> referenceNodesLabels = subGraph.getNodes().stream().map(Node::getLabel).collect(Collectors.toList());
            if (!referenceNodesLabels.contains(solution.getStateName()) && !solution.isOptional()) {
                lastAchievedNode = createVioletNode(subGraph, solution, lastAchievedNode);
                achievedNodesLabels.add(lastAchievedNode.getLabel());
            }
        }
        return subGraph;
    }

    private Node processCommand(Command command, List<DefinitionReferenceSolution> solutions, SubGraph subGraph, Node lastAchievedNode) {
        for (DefinitionReferenceSolution solution : solutions) {
            if (isSolutionCommandSameAsUserCommand(command, solution)) {
                Pair<List<String>, List<String>> requiredAndOptionalNodesNames = sortPrereqNodes(solutions, solution);
                if (achievedNodesLabels.containsAll(requiredAndOptionalNodesNames.getValue0())
                        && (requiredAndOptionalNodesNames.getValue1().isEmpty() || !Collections.disjoint(achievedNodesLabels, requiredAndOptionalNodesNames.getValue1()))) {
                    return createGreenNode(command, subGraph, lastAchievedNode, solution);
                } else {
                    return createYellowNode(command, subGraph, lastAchievedNode, solution);
                }
            }
        }
        createRedNode(command, subGraph, lastAchievedNode);
        return lastAchievedNode;
    }

    private Node createVioletNode(SubGraph subGraph, DefinitionReferenceSolution solution, Node lastAchievedNode) {
        Node newNode = Node.builder()
                .color(GraphConstants.VIOLET)
                .label(solution.getStateName())
                .subGraph(subGraph)
                .build();
        subGraph.getNodes().add(newNode);
        subGraph.getEdges().add(Edge.builder()
                .subGraph(subGraph)
                .color(GraphConstants.VIOLET)
                .fromNode(lastAchievedNode.getLabel())
                .toNode(solution.getStateName())
                .build());
        return newNode;
    }

    private Node createYellowNode(Command command, SubGraph subGraph, Node nodeUnderFocus, DefinitionReferenceSolution solution) {
        String newNodeName = GraphConstants.TRIED_TO_REACH + solution.getStateName() + GraphConstants.MISSING_NODES + solution.getPrereqState();
        Node newNode = Node.builder()
                .color(GraphConstants.YELLOW)
                .label(newNodeName)
                .subGraph(subGraph)
                .build();
        subGraph.getNodes().add(newNode);
        if (nodeUnderFocus.getColor().equals(GraphConstants.GREEN) || nodeUnderFocus.getColor().equals(GraphConstants.VIOLET)) {
            Edge newEdge = Edge.builder()
                    .fromNode(nodeUnderFocus.getLabel())
                    .toNode(newNodeName)
                    .subGraph(subGraph)
                    .tool(command.getCmd())
                    .options(Collections.singleton(command.getOptions()))
                    .type(command.getCommandType())
                    .color(GraphConstants.YELLOW)
                    .build();
            if (!subGraph.getEdges().contains(newEdge)) {
                subGraph.getEdges().add(newEdge);
            }
        } else if (nodeUnderFocus.getColor().equals(GraphConstants.YELLOW)) {
            Edge newEdge = Edge.builder()
                    .fromNode(nodeUnderFocus.getLabel())
                    .toNode(newNodeName)
                    .subGraph(subGraph)
                    .style("dashed")
                    .tool(command.getCmd())
                    .options(Collections.singleton(command.getOptions()))
                    .type(command.getCommandType())
                    .color(GraphConstants.YELLOW)
                    .build();
            if (!subGraph.getEdges().contains(newEdge)) {
                subGraph.getEdges().add(newEdge);
            }
        } else {
            throw new InternalServerErrorException("Error when creating Trainee graph: Focused node must be Yellow or Green");
        }
        return newNode;
    }

    private void createRedNode(Command command, SubGraph subGraph, Node nodeUnderFocus) {
        Node newRedNode = Node.builder()
                .color(GraphConstants.RED)
                .label(GraphConstants.NOT_IN_REFERENCE + nodeUnderFocus.getLabel())
                .subGraph(subGraph)
                .build();
        if (!subGraph.getNodes().contains(newRedNode)) {
            subGraph.getNodes().add(newRedNode);
        }

        Edge newEdge = Edge.builder()
                .fromNode(nodeUnderFocus.getLabel())
                .toNode(GraphConstants.NOT_IN_REFERENCE + nodeUnderFocus.getLabel())
                .subGraph(subGraph)
                .tool(command.getCmd())
                .type(command.getCommandType())
                .color(GraphConstants.RED)
                .build();
        newEdge.getOptions().add(command.getOptions());

        List<Edge> sameEdgeDifferentOptions = subGraph.getEdges().stream()
                .filter(e -> e.getColor().equals(newEdge.getColor()))
                .filter(e -> e.getFromNode().equals(newEdge.getFromNode()))
                .filter(e -> e.getToNode().equals(newEdge.getToNode()))
                .filter(e -> e.getType().equals(newEdge.getType()))
                .filter(e -> e.getTool().equals(newEdge.getTool()))
                .collect(Collectors.toList());
        if (sameEdgeDifferentOptions.isEmpty()) {
            subGraph.getEdges().add(newEdge);
        } else if (sameEdgeDifferentOptions.size() == 1) {
            sameEdgeDifferentOptions.get(0).getOptions().add(command.getOptions());
        } else {
            throw new InternalServerErrorException("Error when creating red edge. There cannot exist more then one for command");
        }
        //backward edge
        if (subGraph.getEdges().stream().noneMatch(e -> e.getFromNode().equals(GraphConstants.NOT_IN_REFERENCE + nodeUnderFocus.getLabel()))) {
            subGraph.getEdges().add(Edge.builder()
                    .fromNode(GraphConstants.NOT_IN_REFERENCE + nodeUnderFocus.getLabel())
                    .toNode(nodeUnderFocus.getLabel())
                    .subGraph(subGraph)
                    .tool("")
                    .type("")
                    .style("dashed")
                    .color(GraphConstants.RED)
                    .build());
        }
    }

    private Node createGreenNode(Command command, SubGraph subGraph, Node nodeUnderFocus, DefinitionReferenceSolution solution) {
        Node newNode = Node.builder()
                .color(GraphConstants.GREEN)
                .label(solution.getStateName())
                .subGraph(subGraph)
                .build();
        subGraph.getNodes().add(newNode);
        achievedNodesLabels.add(newNode.getLabel());

        if (nodeUnderFocus.getColor().equals(GraphConstants.GREEN) || nodeUnderFocus.getColor().equals(GraphConstants.VIOLET)) {
            Edge newEdge = Edge.builder()
                    .fromNode(nodeUnderFocus.getLabel())
                    .toNode(newNode.getLabel())
                    .subGraph(subGraph)
                    .color(GraphConstants.GREEN)
                    .tool(command.getCmd())
                    .options(Collections.singleton(command.getOptions()))
                    .type(command.getCommandType())
                    .build();
            if (!subGraph.getEdges().contains(newEdge) && !newEdge.getFromNode().equals(newEdge.getToNode()))
                subGraph.getEdges().add(newEdge);
        } else if (nodeUnderFocus.getColor().equals(GraphConstants.YELLOW)) {
            Edge newEdge = Edge.builder()
                    .fromNode(nodeUnderFocus.getLabel())
                    .toNode(newNode.getLabel())
                    .subGraph(subGraph)
                    .tool(command.getCmd())
                    .options(Collections.singleton(command.getOptions()))
                    .type(command.getCommandType())
                    .color(GraphConstants.GREEN)
                    .style("dashed")
                    .build();
            if (!subGraph.getEdges().contains(newEdge))
                subGraph.getEdges().add(newEdge);

        } else {
            throw new InternalServerErrorException("Error when creating Trainee graph: Focused node must be Yellow or Green");
        }
        return newNode;
    }

    private boolean isSolutionCommandSameAsUserCommand(Command userCommand, DefinitionReferenceSolution solution) {
        String fullCommand = (userCommand.getCmd() + " " + userCommand.getOptions()).trim();
        boolean commandsEquals = fullCommand.matches(solution.getCmdRegex());
        boolean commandTypesEquals = solution.getCmdType().equals(userCommand.getCommandType());
        return commandsEquals && commandTypesEquals;
    }

    private Node getLastAchievedNode(Graph graph) {
        if (graph.getSubGraphs().isEmpty()) {
            return null;
        }
        SubGraph lastSubgraph = graph.getSubGraphs().get(graph.getSubGraphs().size() - 1);
        return lastSubgraph.getNodes().get(lastSubgraph.getNodes().size() - 1);
    }

    private Pair<List<String>, List<String>> sortPrereqNodes(List<DefinitionReferenceSolution> solutions, DefinitionReferenceSolution solution) {
        List<String> requiredNodesNames = new ArrayList<>();
        final List<String> optionalNodesNames = new ArrayList<>();
        for (String nodeName : solution.getPrereqState()) {
            List<DefinitionReferenceSolution> prereqSolution = solutions.stream().filter(s -> s.getStateName().equals(nodeName)).collect(Collectors.toList());
            if (!prereqSolution.isEmpty()) {
                if (prereqSolution.get(0).isOptional()) {
                    optionalNodesNames.add(prereqSolution.get(0).getStateName());
                } else {
                    requiredNodesNames.add(prereqSolution.get(0).getStateName());
                }
            }
        }
        if (requiredNodesNames.isEmpty() && optionalNodesNames.size() == 1) {
            requiredNodesNames = solutions.stream()
                    .filter(s -> s.getStateName().equals(optionalNodesNames.get(0)))
                    .map(DefinitionReferenceSolution::getPrereqState).collect(Collectors.toList()).get(0);
            optionalNodesNames.remove(0);
        }
        return Pair.with(requiredNodesNames, optionalNodesNames);
    }
}
