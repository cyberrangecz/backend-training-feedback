package cz.muni.ics.kypo.training.feedback.dto.resolver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefinitionReferenceSolution {

    private String cmd;

    @JsonProperty("cmd_type")
    private String cmdType;

    @JsonProperty("state_name")
    private String stateName;

    @JsonProperty("prereq_state")
    private List<String> prereqState;

    @JsonProperty("cmd_regex")
    private String cmdRegex;

    private boolean optional;
}