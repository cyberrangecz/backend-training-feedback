package cz.cyberrange.platform.training.feedback.dto.resolver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefinitionLevel {

    @JsonProperty("reference_solution")
    private List<DefinitionReferenceSolution> definitionReferenceSolutions;
    @JsonProperty("id")
    private Long levelId;
    @JsonProperty("order")
    private Integer levelOrder;
}
