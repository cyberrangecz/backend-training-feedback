package cz.muni.ics.kypo.training.feedback.dto.resolver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DefinitionLevel {

    @JsonProperty("id")
    private Long levelId;

    @JsonProperty("reference_solution")
    List<DefinitionReferenceSolution> definitionReferenceSolutions;
}
