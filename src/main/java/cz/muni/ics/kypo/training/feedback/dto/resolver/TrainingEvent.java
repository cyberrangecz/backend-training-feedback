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

    private String type;

    @JsonDeserialize(using = MillisOrLocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
}
