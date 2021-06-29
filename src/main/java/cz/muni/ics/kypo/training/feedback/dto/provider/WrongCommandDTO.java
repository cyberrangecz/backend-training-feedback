package cz.muni.ics.kypo.training.feedback.dto.provider;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "WrongCommandsDTO", description = "Command with invalid syntax or semantic.", parent = AbstractCommandDTO.class)
@Builder
public class WrongCommandDTO {

    @NotNull
    @ApiModelProperty(value = "Classify command by mistake type into categories.", required = true, example = "SEMANTIC_WRONG_IP")
    private String mistake;

    @NotEmpty
    @ApiModelProperty(value = "Ip address where command was submitted.", required = true, example = "10.10.17.5")
    private String fromHostIp;

    @NotEmpty
    @ApiModelProperty(value = "Valid command options", required = true, example = "-p 25 -a 20.20.15.18")
    private String options;

}
