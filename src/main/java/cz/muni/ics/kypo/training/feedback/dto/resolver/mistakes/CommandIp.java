package cz.muni.ics.kypo.training.feedback.dto.resolver.mistakes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommandIp {
    private String cmd;
    private String ip;

    @JsonCreator
    public CommandIp(@JsonProperty(value = "cmd", required = true) String cmd,
                     @JsonProperty(value = "ip", required = true) String ip) {
        this.cmd = cmd;
        this.ip = ip;
    }
}
