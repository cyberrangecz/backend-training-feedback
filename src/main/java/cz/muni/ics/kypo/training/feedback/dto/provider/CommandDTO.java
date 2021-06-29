package cz.muni.ics.kypo.training.feedback.dto.provider;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "CommandDTO", description = "Command with valid syntax and semantic.", parent = AbstractCommandDTO.class)
public class CommandDTO extends AbstractCommandDTO {

    @Builder
    public CommandDTO(@NotEmpty String commandType, @NotEmpty String cmd, @Past @NotNull LocalDateTime timestamp, @NotEmpty String fromHostIp, @NotEmpty String options) {
        super(commandType, cmd);
        this.timestamp = timestamp;
        this.fromHostIp = fromHostIp;
        this.options = options;
    }

    @Past
    @NotNull
    @ApiModelProperty(value = "Time when command was recorded.", required = true, example = "07:23:43")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "hh:mm:ss")
    private LocalDateTime timestamp;

    @NotEmpty
    @ApiModelProperty(value = "Ip address where command was submitted.", required = true, example = "10.10.17.5")
    private String fromHostIp;

    @NotEmpty
    @ApiModelProperty(value = "Valid command options", required = true, example = "-p 25 -a 20.20.15.18")
    private String options;

}
