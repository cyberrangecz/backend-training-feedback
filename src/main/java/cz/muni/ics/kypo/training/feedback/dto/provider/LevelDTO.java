package cz.muni.ics.kypo.training.feedback.dto.provider;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "LevelDTO", description = "Level started by trainee")
public class LevelDTO {


    @NotNull
    @ApiModelProperty(value = "Id of level.", required = true, example = "11")
    private Long levelId;
}
