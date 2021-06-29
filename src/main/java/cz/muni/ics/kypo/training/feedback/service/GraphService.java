package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.model.Graph;
import cz.muni.ics.kypo.training.feedback.repository.GraphRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;


@RequiredArgsConstructor
@Service
@Transactional
public class GraphService extends CRUDServiceImpl<Graph, Long> {

    private final GraphRepository graphRepository;

    @Override
    public JpaRepository<Graph, Long> getDAO() {
        return graphRepository;
    }

    public Graph getGraphByTraineeSandboxId(Long sandboxId) {
        Graph graph = graphRepository.findByTraineeSandboxId(sandboxId);
        if (graph == null) {
            throw new NoSuchElementException("Trainee graph for trainee with sandboxId " + sandboxId.toString() + " does not exist.");
        }
        return graph;
    }

    public Graph getGraphByLabel(String label) {
        List<Graph> graphs = graphRepository.findByLabel(label);
        if (graphs == null || graphs.size() == 0) {
            throw new NoSuchElementException("Graph with label " + label + " does not exist.");
        }
        if (graphs.size() != 1) {
            throw new NoSuchElementException("There exist more then 1 graph with label: " + label);
        }
        return graphs.get(0);
    }


}
