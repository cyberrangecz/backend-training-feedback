package cz.muni.ics.kypo.training.feedback.dto.provider;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@ApiModel(value = "AggregatedCommandsDTO", description = "Contains commands aggregated by command type and command", parent = AbstractCommandDTO.class)
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedCommandsDTO extends AbstractCommandDTO implements Comparable<AggregatedCommandsDTO> {

    @ApiModelProperty(value = "Count of all usages of same command regardless to command options.", required = true, example = "25")
    private Long frequency;
    @ApiModelProperty(value = "List of incorrect commands with same command and command type", required = true, example = "-vvv: 12, \n -a: 22")
    private List<CommandPerOptions> aggregatedCommandsPerOptions;

    @Builder
    public AggregatedCommandsDTO(@NotEmpty String commandType, @NotEmpty String cmd, Long frequency, List<CommandPerOptions> aggregatedCommandsPerOptions) {
        super(commandType, cmd);
        this.frequency = frequency;
        this.aggregatedCommandsPerOptions = aggregatedCommandsPerOptions;
    }

    @Override
    public int compareTo(AggregatedCommandsDTO o) {
        return this.frequency.compareTo(o.frequency);
    }
}
