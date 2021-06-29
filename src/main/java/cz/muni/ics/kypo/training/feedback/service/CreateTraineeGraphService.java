package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionReferenceSolution;
import cz.muni.ics.kypo.training.feedback.exceptions.InternalServerErrorException;
import cz.muni.ics.kypo.training.feedback.constants.GraphConstants;
import cz.muni.ics.kypo.training.feedback.model.Command;
import cz.muni.ics.kypo.training.feedback.model.Edge;
import cz.muni.ics.kypo.training.feedback.model.Graph;
import cz.muni.ics.kypo.training.feedback.model.Level;
import cz.muni.ics.kypo.training.feedback.model.Node;
import cz.muni.ics.kypo.training.feedback.model.SubGraph;
import cz.muni.ics.kypo.training.feedback.model.Trainee;
import cz.muni.ics.kypo.training.feedback.service.api.ElasticsearchServiceApi;
import lombok.RequiredArgsConstructor;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateTraineeGraphService {

    private final ElasticsearchServiceApi elasticsearchServiceApi;

    private final List<String> achievedNodesLabels = new ArrayList<>();

    Graph createTraineeGraph(Trainee trainee, Long sandboxId) {
        List<DefinitionLevel> definitionLevels = elasticsearchServiceApi.getDefinitionLevels();
        Graph graph = Graph.builder().trainee(trainee).label(GraphConstants.TRAINEE_GRAPH_LABEL + sandboxId).build();
        long visibleLevelId = GraphConstants.FIRST_LEVEL_ID;

        for (Level level : trainee.getLevels()) {
            Node lastAchievedNode = getLastAchievedNode(graph);
            List<List<DefinitionReferenceSolution>> levelSolutionsList = definitionLevels.stream()
                    .filter(l -> l.getLevelId().equals(level.getId()))
                    .map(DefinitionLevel::getDefinitionReferenceSolutions)
                    .collect(Collectors.toList());
            if (levelSolutionsList.size() == 1) {
                List<DefinitionReferenceSolution> levelSolutions = levelSolutionsList.get(0);
                SubGraph newSubgraph = processLevelCommands(level, levelSolutions, visibleLevelId++, lastAchievedNode);
                newSubgraph.setGraph(graph);
                graph.getSubGraphs().add(newSubgraph);
            } else if (levelSolutionsList.size() > 1) {
                throw new InternalServerErrorException("Error when creating Trainee graph: Exist more reference solution levels with same id.");
            }
        }
        return graph;
    }

    private SubGraph processLevelCommands(Level level, List<DefinitionReferenceSolution> levelSolutions, Long visibleLevelId, Node lastAchievedNode) {
        List<Command> commands = level.getCommands();
        SubGraph subGraph = SubGraph.builder().label("Level: " + visibleLevelId.toString()).build();
        Node startNode = Node.builder()
                .subGraph(subGraph)
                .color(GraphConstants.VIOLET)
                .shape(GraphConstants.DIAMOND)
                .label("level_" + visibleLevelId + "_start")
                .build();
        subGraph.getNodes().add(startNode);
        if (lastAchievedNode != null) {
            subGraph.getEdges().add(Edge.builder()
                    .subGraph(subGraph)
                    .toNode("level_" + visibleLevelId + "_start")
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
                    .options(Collections.singletonList(command.getOptions()))
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
                    .options(Collections.singletonList(command.getOptions()))
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
        if (subGraph.getEdges().stream().noneMatch(e -> e.getFromNode().equals(GraphConstants.NOT_IN_REFERENCE + nodeUnderFocus.getLabel()))){
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
                    .options(Collections.singletonList(command.getOptions()))
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
                    .options(Collections.singletonList(command.getOptions()))
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
