package cz.muni.ics.kypo.training.feedback.dto.resolver.mistakes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyntaxArgument {

    private Long order;

    private String value;
    @JsonProperty(required = true)
    private String placement;

    @JsonCreator
    public SyntaxArgument(@JsonProperty(value = "order", required = true) Long order,
                          @JsonProperty(value = "value", required = true) String value,
                          @JsonProperty(value = "placement", required = true) String placement) {
        this.order = order;
        this.value = value;
        this.placement = placement;
    }
}
