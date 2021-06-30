package cz.muni.ics.kypo.training.feedback.dto.provider;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;


@ApiModel(value = "AggregatedCommandDTO", description = "Contains info about options and their granularity for some command.", parent = AbstractCommandDTO.class)
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedCommandDTO extends AbstractCommandDTO implements Comparable<AggregatedCommandDTO> {

    @NotNull
    @ApiModelProperty(value = "Count of all usages of same command regardless to command options.", required = true, example = "25")
    private Long frequency;
    @NotNull
    @ApiModelProperty(value = "Count of command usages for every options.", required = true, example = "-vvv: 12, \n -a: 22")
    private Map<String, Long> granularityPerOption;

    @Builder
    public AggregatedCommandDTO(@NotEmpty String commandType, @NotEmpty String cmd, @NotNull Long frequency, @NotNull Map<String, Long> granularityPerOption) {
        super(commandType, cmd);
        this.frequency = frequency;
        this.granularityPerOption = granularityPerOption;
    }

    @Override
    public int compareTo(AggregatedCommandDTO o) {
        return this.frequency.compareTo(o.frequency);
    }
}
