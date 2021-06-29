package cz.muni.ics.kypo.training.feedback.dto.provider;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@ApiModel(value = "AggregatedWrongCommandsDTO", description = "Contains wrong commands aggregated by command type and command", parent = AbstractCommandDTO.class)
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedWrongCommandsDTO extends AbstractCommandDTO  implements Comparable<AggregatedWrongCommandsDTO> {

    @Builder
    public AggregatedWrongCommandsDTO(@NotEmpty String commandType, @NotEmpty String cmd, Long frequency, List<WrongCommandDTO> wrongCommandDTOS) {
        super(commandType, cmd);
        this.frequency = frequency;
        this.wrongCommandDTOS = wrongCommandDTOS;
    }

    @ApiModelProperty(value = "Count of all usages of same command regardless to command options.", required = true, example = "25")
    private Long frequency;
    @ApiModelProperty(value = "List of wrong commands with same command and command type", required = true, example = "-vvv: 12, \n -a: 22")
    private List<WrongCommandDTO> wrongCommandDTOS;

    @Override
    public int compareTo(AggregatedWrongCommandsDTO o) {
        return this.frequency.compareTo(o.frequency);
    }
}
