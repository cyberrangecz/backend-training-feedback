package cz.cyberrange.platform.training.feedback.dto.resolver.mistakes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TrainingSemantic {

    private List<String> attackers;
    private List<String> targets;
    private List<String> addresses;
    private List<String> msfExploits;
    private Map<String, List<String>> users;
    private List<CommandIp> commandIps;

    @JsonCreator
    public TrainingSemantic(@JsonProperty(value = "attackers", required = true) List<String> attackers,
                            @JsonProperty(value = "targets", required = true) List<String> targets,
                            @JsonProperty(value = "addresses", required = true) List<String> addresses,
                            @JsonProperty(value = "msf-exploits", required = true) List<String> msfExploits,
                            @JsonProperty(value = "users", required = true) Map<String, List<String>> users,
                            @JsonProperty(value = "commands-ips", required = true) List<CommandIp> commandIps) {
        this.attackers = attackers;
        this.targets = targets;
        this.addresses = addresses;
        this.msfExploits = msfExploits;
        this.users = users;
        this.commandIps = commandIps;
    }
}
