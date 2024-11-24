package cz.cyberrange.platform.training.feedback.dto.resolver.mistakes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SyntaxCommandOption {

    private boolean caseSensitive;
    private List<String> longName;
    private List<String> shortNames;
    private List<SyntaxOptionParameter> parameters;
    private List<String> validWith;

    @JsonCreator
    public SyntaxCommandOption(@JsonProperty(value = "case-sensitive", required = true) boolean caseSensitive,
                               @JsonProperty(value = "long-names", required = true) List<String> longName,
                               @JsonProperty(value = "short-names", required = true) List<String> shortNames,
                               @JsonProperty(value = "parameters", required = true) List<SyntaxOptionParameter> parameters,
                               @JsonProperty(value = "valid-with", required = true) List<String> validWith) {
        this.caseSensitive = caseSensitive;
        this.longName = longName;
        this.shortNames = shortNames;
        this.parameters = parameters;
        this.validWith = validWith;
    }
}
