package cz.muni.ics.kypo.training.feedback.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Edge {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edgeGenerator")
    @SequenceGenerator(name = "edgeGenerator", sequenceName = "edge_id_seq")
    @Column(name = "edge_id", nullable = false, unique = true)
    private Long id;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @EqualsAndHashCode.Exclude
    private SubGraph subGraph;
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "options")
    @Column(name="option")
    private List<String> options = new ArrayList<>();
    @NotEmpty
    @Column(name = "from_node", nullable = false)
    private String fromNode;
    @NotEmpty
    @Column(name = "to_node", nullable = false)
    private String toNode;
    @NotNull
    @Builder.Default
    @Column(name = "weight")
    private Long weight = 5L;
    @NotNull
    @Builder.Default
    @Column(name = "length")
    private Double length = 2.0;
    @Builder.Default
    @Column(name = "type")
    private String type = "";
    @Builder.Default
    @Column(name = "tool")
    private String tool = "";
    @NotEmpty
    @Builder.Default
    @Column(name = "color", nullable = false)
    private String color = "#000000";
    @Builder.Default
    @NotEmpty
    @Column(name = "style", nullable = false)
    private String style = "solid";

    @Override
    public String toString() {
        return String.format(String.format("\"%%s\" -> \"%%s\" [label=\"%s%s%s\"  color=\"%%s\" style=\"%%s\" weight=\"%%d\" length=\"%%f\"]%%n", toolToString(), typeToString(), optionsToString())
                , fromNode, toNode, color, style, weight, length);
    }

    private String toolToString() {
        if (tool.isBlank()) {
            return "";
        }
        return "Tool: " + tool + " %n ";
    }

    private String optionsToString() {
        if (options == null || options.isEmpty()) {
            return "";
        }
        return "Opts: " + options;
    }

    private String typeToString() {
        if (type.isBlank()) {
            return "";
        }
        return "Type: " + type + " %n ";

    }

}