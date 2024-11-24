package cz.cyberrange.platform.training.feedback.dto.provider;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@ApiModel(value = "AbstractCommandDTO", subTypes = {AggregatedCommandsDTO.class, CommandDTO.class},
        description = "AggregatedCommandsDTO, CommandDTO")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AggregatedCommandsDTO.class, name = "AggregatedCommandsDTO"),
        @JsonSubTypes.Type(value = CommandDTO.class, name = "CommandDTO")})
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractCommandDTO {

    @ApiModelProperty(value = "Distinguish tool/command line  in which command ran.", required = true, example = "BASH")
    @NotEmpty
    public String commandType;

    @ApiModelProperty(value = "Command without arguments/options.", required = true, example = "ls")
    @NotEmpty
    public String cmd;
}
