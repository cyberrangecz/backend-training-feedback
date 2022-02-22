package cz.muni.ics.kypo.training.feedback.service.api;

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

    public List<TrainingCommand> getTrainingCommandsByPool(Long poolId) {
        try {
            return elasticsearchServiceWebClient
                    .get()
                    .uri("/training-platform-commands/pools/{poolId}", poolId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TrainingCommand>>() {
                    })
                    .block();
        } catch (CustomWebClientException ex) {
            throw new MicroserviceApiException("Error when calling Elasticsearch API to get training commands for particular pool (ID: " + poolId + ").", ex);
        }
    }

    public List<TrainingCommand> getTrainingCommandsByAccessToken(String accessToken) {
        try {
            return elasticsearchServiceWebClient
                    .get()
                    .uri("/training-platform-commands/access-tokens/{accessToken}", accessToken)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TrainingCommand>>() {
                    })
                    .block();
        } catch (CustomWebClientException ex) {
            throw new MicroserviceApiException("Error when calling Elasticsearch API to delete bash commands for particular training instance (access-token: " + accessToken +").", ex);
        }
    }

    public List<TrainingCommand> getTrainingCommandsBySandboxId(Long sandboxId) {
        try {
            return elasticsearchServiceWebClient
                    .get()
                    .uri("/training-platform-commands/sandboxes/{sandboxId}", sandboxId)
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

    public List<TrainingCommand> getTrainingCommandsByAccessTokenAndUserId(String accessToken, Long userId) {
        try {
            return elasticsearchServiceWebClient
                    .get()
                    .uri("/training-platform-commands/access-tokens/{accessToken}/users/{userId}", accessToken, userId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TrainingCommand>>() {
                    })
                    .block()
                    .stream()
                    //some bug that lots of "empty" not valid command exist
                    .filter(c -> c.getTimestamp() != null)
                    .collect(Collectors.toList());
        } catch (CustomWebClientException ex) {
            throw new MicroserviceApiException("Error when calling Elasticsearch API for particular training " +
                    "(access-token: " + accessToken +")" + "(user: " + userId +").", ex);
        }
    }


    public List<TrainingEvent> getTrainingEventsByTrainingRunId(Long definitionId, Long instanceId, Long runId) {
        try {
            return elasticsearchServiceWebClient
                    .get()
                    .uri("/training-platform-events/training-definitions/{definitionId}/training-instances/{instanceId}/training-runs/{runId}", definitionId, instanceId, runId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TrainingEvent>>() {
                    })
                    .block();
        } catch (CustomWebClientException ex) {
            throw new MicroserviceApiException("Error when calling Elasticsearch API to get training events for particular training run (ID: " + runId + ").", ex);
        }
    }
}
