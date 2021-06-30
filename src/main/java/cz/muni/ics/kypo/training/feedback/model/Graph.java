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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Graph {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "graphGenerator")
    @SequenceGenerator(name = "graphGenerator", sequenceName = "graph_id_seq")
    @Column(name = "graph_id", nullable = false, unique = true)
    private Long id;
    @OneToOne(mappedBy = "traineeGraph",
            optional = true,
            cascade = CascadeType.PERSIST,
            fetch = FetchType.EAGER)
    private Trainee trainee;
    @NotEmpty
    @Builder.Default
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "graph",
            targetEntity = SubGraph.class,
            cascade = CascadeType.PERSIST,
            fetch = FetchType.EAGER)
    private List<SubGraph> subGraphs = new ArrayList<>();

    @NotEmpty
    @Column(name = "label", nullable = false)
    private String label;
    @NotEmpty
    @Builder.Default
    @Column(name = "label_location", nullable = false)
    private String labelLocation = "t";
    @NotNull
    @Builder.Default
    @Column(name = "font_size", nullable = false)
    private Double fontSize = 30.0;

    public String toString() {
        String header = String.format("%s { %n tooltip=\" \" %n graph [fontsize=\"%f\"]%n",
                GraphConstants.GRAPH_TYPE, fontSize);
        StringBuilder ret = new StringBuilder();
        ret.append(header);
        subGraphs.forEach(subGraph -> ret.append(subGraph.toString()));
        ret.append(GraphConstants.GRAPH_FOOTER);
        return ret.toString();
    }
}
