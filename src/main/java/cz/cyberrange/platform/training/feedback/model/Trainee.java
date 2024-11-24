package cz.cyberrange.platform.training.feedback.model;

import lombok.*;

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
    @ToString.Exclude
    @NotNull
    @OneToOne(mappedBy = "trainee",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.LAZY)
    private Graph traineeGraph;
    @ToString.Exclude
    @NotEmpty
    @Builder.Default
    @OneToMany(mappedBy = "trainee",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            fetch = FetchType.LAZY)
    private List<Level> levels = new ArrayList<>();
    @NotNull
    @Column(name = "training_run_id", unique = true)
    private Long trainingRunId;
    @NotNull
    @Column(name = "user_ref_id", unique = true)
    private Long userRefId;
    @Column(name = "sandbox_id", length = 36)
    private String sandboxId;
}
