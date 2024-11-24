package cz.cyberrange.platform.training.feedback.dto.resolver.mistakes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyntaxOptionParameter {

    private boolean optional;

    private String value;

    @JsonCreator
    public SyntaxOptionParameter(@JsonProperty(value = "optional", required = true) boolean optional,
                                 @JsonProperty(value = "value", required = true) String value) {
        this.optional = optional;
        this.value = value;
    }
}
