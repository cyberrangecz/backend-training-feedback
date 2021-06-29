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
public class Trainee {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "traineeGenerator")
    @SequenceGenerator(name = "traineeGenerator", sequenceName = "trainee_id_seq")
    @Column(name = "trainee_id", nullable = false, unique = true)
    private Long id;
    @NotNull
    @OneToOne(cascade = CascadeType.PERSIST,
            fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private Graph traineeGraph;
    @NotEmpty
    @Builder.Default
    @OneToMany(mappedBy = "trainee",
            targetEntity = Level.class,
            cascade = CascadeType.PERSIST,
            fetch = FetchType.LAZY)
    private List<Level> levels = new ArrayList<>();
    @NotNull
    @Column(name = "sandbox_id", unique = true)
    private Long sandboxId;
}
