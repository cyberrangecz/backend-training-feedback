package cz.muni.ics.kypo.training.feedback.model;

import cz.muni.ics.kypo.training.feedback.constants.GraphConstants;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Node {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nodeGenerator")
    @SequenceGenerator(name = "nodeGenerator", sequenceName = "node_id_seq")
    @Column(name = "node_id", nullable = false, unique = true)
    private Long id;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "sub_graph_id")
    @EqualsAndHashCode.Exclude
    private SubGraph subGraph;
    @NotEmpty
    @Column(name = "label", nullable = false)
    private String label;
    @Column(name = "name")
    private String name;
    @NotEmpty
    @Column(name = "color", nullable = false)
    private String color;
    @NotEmpty
    @Builder.Default
    @Column(name = "shape", nullable = false)
    private String shape = "ellipse";

    @Override
    public String toString() {
        return String.format("\"%s\" [label=\"%s\" style=\"%s\" shape=\"%s\" fillcolor=\"%s\"]%n", name != null ? name : label, label, GraphConstants.NODE_STYLE, shape, color);
    }

    @Override
    protected Node clone() {
        Node clonedNode = new Node();
        clonedNode.setColor(this.color);
        clonedNode.setLabel(this.label);
        clonedNode.setName(this.name);
        clonedNode.setShape(this.shape);
        return clonedNode;
    }
}
