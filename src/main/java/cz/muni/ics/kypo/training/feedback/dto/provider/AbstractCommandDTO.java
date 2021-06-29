package cz.muni.ics.kypo.training.feedback.dto.provider;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@ApiModel(value = "AbstractCommandDTO", subTypes = {AggregatedCommandDTO.class, WrongCommandDTO.class, CommandDTO.class},
        description = "AggregatedCommandDTO, WrongCommandsDTO, CommandDTO")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AggregatedCommandDTO.class, name = "AggregatedCommandDTO"),
        @JsonSubTypes.Type(value = WrongCommandDTO.class, name = "WrongCommandsDTO"),
        @JsonSubTypes.Type(value = CommandDTO.class, name = "CommandDTO")})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractCommandDTO {

    @ApiModelProperty(value = "Distinguish tool/command line  in which command ran.", required = true, example = "BASH")
    @NotEmpty
    public String commandType;

    @ApiModelProperty(value = "Command without arguments/options.", required = true, example = "ls")
    @NotEmpty
    public String cmd;
}
