package cz.muni.ics.kypo.training.feedback.dto.resolver.mistakes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyntaxArgumentSet {

    private Long argumentSetId;
    private List<SyntaxArgument> values;

    @JsonCreator
    public SyntaxArgumentSet(@JsonProperty(value = "argument-set-id", required = true) Long argumentSetId,
                             @JsonProperty(value = "argument-set-values", required = true) List<SyntaxArgument> values) {
        this.argumentSetId = argumentSetId;
        this.values = values;
    }
}
