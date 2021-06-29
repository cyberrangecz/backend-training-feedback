package cz.muni.ics.kypo.training.feedback.model;

import cz.muni.ics.kypo.training.feedback.constants.GraphConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(nullable = false)
    private Graph graph;
    @NotEmpty
    @Builder.Default
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "subGraph",
            targetEntity = Node.class,
            cascade = CascadeType.PERSIST,
            fetch = FetchType.EAGER)
    private List<Node> nodes = new ArrayList<>();
    @NotEmpty
    @Builder.Default
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "subGraph",
            targetEntity = Edge.class,
            cascade = CascadeType.PERSIST,
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
}
