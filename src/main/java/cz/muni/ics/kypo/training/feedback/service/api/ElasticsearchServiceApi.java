package cz.muni.ics.kypo.training.feedback.service.api;

import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.dto.resolver.TrainingCommand;
import cz.muni.ics.kypo.training.feedback.dto.resolver.TrainingEvent;
import cz.muni.ics.kypo.training.feedback.exceptions.CustomWebClientException;
import cz.muni.ics.kypo.training.feedback.exceptions.MicroserviceApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElasticsearchServiceApi {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchServiceApi.class);
    private final WebClient elasticsearchServiceWebClient;

    public List<TrainingCommand> getTrainingCommands(Long poolId) {
        try {
            return elasticsearchServiceWebClient
                    .delete()
                    .uri("/pools/{poolId}", poolId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TrainingCommand>>() {
                    })
                    .block();
        } catch (CustomWebClientException ex) {
            throw new MicroserviceApiException("Error when calling Elasticsearch API to get training commands for particular pool (ID: " + poolId + ").", ex);
        }
    }

    public List<TrainingCommand> getTrainingCommandsBySandboxId(Long sandboxId) {
        try {
            return elasticsearchServiceWebClient
                    .delete()
                    .uri("/sandboxes/{sandboxId}", sandboxId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TrainingCommand>>() {
                    })
                    .block()
                    .stream()
                    //some bug that lots of "empty" not valid command exist
                    .filter(c -> c.getTimestamp() != null)
                    .collect(Collectors.toList());
        } catch (CustomWebClientException ex) {
            throw new MicroserviceApiException("Error when calling Elasticsearch API to get training commands for particular sandbox (ID: " + sandboxId + ").", ex);
        }
    }


    public List<TrainingEvent> getAllTrainingEvents(Long definitionId, Long instanceId) {
        try {
            return elasticsearchServiceWebClient
                    .delete()
                    .uri("training-definitions/{definitionId}/training-instances/{instanceId}", definitionId, instanceId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TrainingEvent>>() {
                    })
                    .block();
        } catch (CustomWebClientException ex) {
            throw new MicroserviceApiException("Error when calling Elasticsearch API to get training events for particular instance (ID: " + instanceId + ").", ex);
        }
    }

    public List<TrainingEvent> getTrainingEventsBySandboxId(Long definitionId, Long instanceId, Long sandboxId) {
        List<TrainingEvent> allEvents = getAllTrainingEvents(definitionId, instanceId);
        return allEvents.stream()
                .filter(e -> e.getSandboxId().equals(sandboxId))
                .collect(Collectors.toList());
    }
}
