package cz.muni.ics.kypo.training.feedback.dto.resolver;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingCommand {

    @JsonProperty("sandbox_id")
    private Long sandboxId;

    @NotNull
    private LocalDateTime timestamp;

    @JsonProperty("cmd_type")
    @NotNull
    private String cmdType;
    @NotNull
    private String command;
    @NotNull
    private String commandArguments;

    private String hostname;

    private String username;

    private String wd; //working directory

    private String ip;

    @JsonProperty("cmd")
    private void deserializeCmd(String command) {
        List<String> commandList = Arrays.asList(command.split(" ").clone());
        this.command = commandList.get(0);
        this.commandArguments = String.join(" ", commandList.subList(1, commandList.size()));

    }

    @JsonProperty("timestamp_str")
    private void deserializeTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        this.timestamp = LocalDateTime.parse(time.substring(0, 19), formatter);
    }

}
