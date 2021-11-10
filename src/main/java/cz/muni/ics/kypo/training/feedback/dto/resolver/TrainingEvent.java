package cz.muni.ics.kypo.training.feedback.dto.resolver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.muni.ics.kypo.training.feedback.converter.MillisOrLocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingEvent {

    private Long level;

    @JsonProperty("sandbox_id")
    private Long sandboxId;

    @JsonProperty("training_run_id")
    private Long trainingRunId;

    @JsonProperty("training_instance_id")
    private Long trainingInstanceId;

    @JsonProperty("training_definition_id")
    private Long trainingDefinitionId;

    @JsonProperty("user_ref_id")
    private Long userRefId;

    private String type;

    @JsonDeserialize(using = MillisOrLocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
}
