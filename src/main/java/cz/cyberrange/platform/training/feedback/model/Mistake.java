package cz.cyberrange.platform.training.feedback.model;

import cz.cyberrange.platform.training.feedback.enums.MistakeType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mistake {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mistakeGenerator")
    @SequenceGenerator(name = "mistakeGenerator", sequenceName = "mistake_id_seq")
    @Column(name = "mistake_id", nullable = false, unique = true)
    private Long id;
    @Builder.Default
    @OneToMany(mappedBy = "mistake",
            fetch = FetchType.EAGER)
    private List<Command> commands = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(name = "mistake_type")
    private MistakeType mistakeType;

    public Mistake(List<Command> commands, MistakeType mistakeType) {
        this.commands = commands;
        this.mistakeType = mistakeType;
    }
}
