package cz.muni.ics.kypo.training.feedback.model;

import cz.muni.ics.kypo.training.feedback.constants.GraphConstants;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "sub_graph")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubGraph {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subGraphGenerator")
    @SequenceGenerator(name = "subGraphGenerator", sequenceName = "sub_graph_id_seq")
    @Column(name = "sub_graph_id", nullable = false, unique = true)
    private Long id;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "graph_id")
    private Graph graph;
    @Builder.Default
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "subGraph",
            targetEntity = Node.class,
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
            fetch = FetchType.EAGER)
    private List<Node> nodes = new ArrayList<>();
    @Builder.Default
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "subGraph",
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
            fetch = FetchType.EAGER)
    private List<Edge> edges = new ArrayList<>();
    @Builder.Default
    @NotEmpty
    @Column(name = "color", nullable = false)
    private String color = GraphConstants.LIGHTGREY;
    @NotEmpty
    @Column(name = "label", nullable = false)
    private String label;

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        String header = String.format("subgraph cluster_%d { tooltip=\" \" %n %n label = \"%s\"; %n color=%s; %n", id, label, color);
        ret.append(header);
        nodes.forEach(node -> ret.append(node.toString()));
        edges.forEach(edge -> ret.append(edge.toString()));
        ret.append(GraphConstants.GRAPH_FOOTER);
        return ret.toString();
    }

    @Override
    public SubGraph clone() {
        SubGraph clonedSubGraph = new SubGraph();
        clonedSubGraph.setColor(this.color);
        clonedSubGraph.setLabel(this.label);
        clonedSubGraph.setEdges(this.edges.stream()
                .map(edge -> {
                    Edge clonedEdge = edge.clone();
                    clonedEdge.setSubGraph(clonedSubGraph);
                    return clonedEdge;
                }).collect(Collectors.toList()));
        clonedSubGraph.setNodes(this.nodes.stream()
                .map(node -> {
                    Node clonedNode = node.clone();
                    clonedNode.setSubGraph(clonedSubGraph);
                    return clonedNode;
                }).collect(Collectors.toList()));
        return clonedSubGraph;
    }
}
