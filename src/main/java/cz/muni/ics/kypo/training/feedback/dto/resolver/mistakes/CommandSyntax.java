package cz.muni.ics.kypo.training.feedback.dto.resolver.mistakes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CommandSyntax {
    private List<String> aliases;
    @JsonIgnore
    private String cmdType;
    private List<SyntaxArgumentSet> arguments;
    private List<SyntaxCommandOption> options;

    @JsonCreator
    public CommandSyntax(@JsonProperty(value = "aliases", required = false) List<String> aliases,
                         @JsonProperty(value = "arguments", required = true) List<SyntaxArgumentSet> arguments,
                         @JsonProperty(value = "options", required = true) List<SyntaxCommandOption> options) {
        this.aliases = aliases;
        this.arguments = arguments;
        this.options = options;
    }
}
