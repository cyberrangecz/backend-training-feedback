package cz.cyberrange.platform.training.feedback.repository;

import cz.cyberrange.platform.training.feedback.enums.GraphType;
import cz.cyberrange.platform.training.feedback.model.Graph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GraphRepository extends JpaRepository<Graph, Long> {

    Graph findByTraineeTrainingRunId(Long trainingRunId);

    @Query("SELECT g FROM Graph g " +
            "WHERE g.trainingInstanceId = :trainingInstanceId " +
            "AND g.graphType LIKE 'SUMMARY_GRAPH'")
    Graph findSummaryGraph(Long trainingInstanceId);

    @Query("SELECT g FROM Graph g " +
            "WHERE g.trainingDefinitionId = :trainingDefinitionId " +
            "AND g.graphType LIKE 'REFERENCE_GRAPH'")
    Graph findReferenceGraph(Long trainingDefinitionId);

    @Query("SELECT g FROM Graph g " +
            "JOIN FETCH g.trainee t " +
            "WHERE g.graphType LIKE 'TRAINEE_GRAPH' AND " +
            "t.trainingRunId = :trainingRunId ")
    Graph findTraineeGraph(Long trainingRunId);

    boolean existsByTrainingDefinitionIdAndGraphType(Long definitionId, GraphType graphType);

    boolean existsByTrainingInstanceIdAndGraphType(Long instanceId, GraphType graphType);

    boolean existsByTraineeTrainingRunId(Long runId);

    void deleteByTrainingDefinitionIdAndGraphType(Long definitionId, GraphType graphType);

    void deleteByTrainingInstanceIdAndGraphType(Long instanceId, GraphType graphType);

    void deleteByTraineeTrainingRunId(Long runId);

    List<Graph> getAllByTrainingDefinitionIdAndTrainingInstanceId(Long definitionId, Long instanceId);
}
