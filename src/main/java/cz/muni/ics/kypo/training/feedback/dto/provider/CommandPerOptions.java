package cz.muni.ics.kypo.training.feedback.dto.provider;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel(value = "CommandPerOptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandPerOptions {

    @ApiModelProperty(value = "Command without arguments/options.", required = true, example = "ls")
    @NotEmpty
    public String cmd;
    @ApiModelProperty(value = "Distinguish tool/command line  in which command ran.", required = true, example = "BASH")
    @NotEmpty
    public String commandType;
    @NotEmpty
    @ApiModelProperty(value = "Valid command options", required = true, example = "-p 25 -a 20.20.15.18")
    private String options;
    @ApiModelProperty(value = "Classify command by mistake type into categories.", required = true, example = "SEMANTIC_WRONG_IP")
    private MistakeType mistake;
    @NotEmpty
    @ApiModelProperty(value = "Ip address where command was submitted.", required = true, example = "10.10.17.5")
    private String fromHostIp;
    @NotNull
    @ApiModelProperty(value = "Count of all usages of same command regardless to command options.", required = true, example = "25")
    private Long frequency;

    public CommandPerOptions(String cmd, String commandType, String options, String fromHostIp, Long frequency) {
        this(cmd, commandType, options, null, fromHostIp, frequency);
    }
}
