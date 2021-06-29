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
@ApiModel(value = "TraineeDTO", description = "Trainee which absolved training.")
public class TraineeDTO {

    @NotNull
    @ApiModelProperty(value = "Id of trainee sandbox.", required = true, example = "335")
    private Long sandboxId;

}
