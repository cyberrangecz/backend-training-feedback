package cz.muni.ics.kypo.training.feedback.dto.provider;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "GraphDTO", description = "Graph in DOT format.")
public class GraphDTO {

    @NotEmpty
    @ApiModelProperty(value = "Graph in DOT format.", example = "'digraph {a -> b}'", required = true)
    private String graph;

}
