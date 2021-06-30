package cz.muni.ics.kypo.training.feedback.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Command {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commandGenerator")
    @SequenceGenerator(name = "commandGenerator", sequenceName = "command_id_seq")
    @Column(name = "command_id", nullable = false, unique = true)
    private Long id;
    @NotNull
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Level level;
    @ManyToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST)
    @JoinColumn(nullable = true)
    private Mistake mistake;
    @NotNull
    @Column(columnDefinition = "TIMESTAMP", name = "timestamp")
    private LocalDateTime timestamp;
    @NotEmpty
    @Column(name = "cmd", nullable = false)
    private String cmd;
    @NotEmpty
    @Column(name = "command_type", nullable = false)
    private String commandType;
    @Column(name = "options")
    private String options;
    @Column(name = "uname")
    private String uname;
    @Column(name = "wd")
    private String wd;
    @Column(name = "from_host_ip")
    private String fromHostIp;

}
