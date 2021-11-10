package cz.muni.ics.kypo.training.feedback.model;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Level {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "levelGenerator")
    @SequenceGenerator(name = "levelGenerator", sequenceName = "level_id_seq")
    @Column(name = "level_id", nullable = false, unique = true)
    private Long id;
    @ToString.Exclude
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "trainee_id")
    private Trainee trainee;
    @ToString.Exclude
    @NotNull
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "level",
            targetEntity = Command.class,
            cascade = { CascadeType.PERSIST, CascadeType.REMOVE },
            fetch = FetchType.LAZY)
    private List<Command> commands;
    @NotNull
    @Column(name = "level_ref_id", nullable = false)
    private Long levelRefId;
    @NotNull
    @Column(columnDefinition = "TIMESTAMP", name = "start_time", nullable = false)
    private LocalDateTime startTime;
    //could be null, it signalize that level was not completed
    @Column(columnDefinition = "TIMESTAMP", name = "end_time")
    private LocalDateTime endTime;
}
