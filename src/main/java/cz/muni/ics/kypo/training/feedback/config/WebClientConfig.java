package cz.muni.ics.kypo.training.feedback.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.ics.kypo.training.feedback.exceptions.CustomWebClientException;
import cz.muni.ics.kypo.training.feedback.exceptions.errors.JavaApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * The type Web api config.
 */
@Configuration
@Import(ObjectMappersConfiguration.class)
public class WebClientConfig {


    private final ObjectMapper objectMapper;
    @Value("${user-and-group-server.uri}")
    private String userAndGroupURI;
    @Value("${elasticsearch-service.uri}")
    private String elasticsearchServiceURI;

    @Autowired
    public WebClientConfig(@Qualifier("webClientObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * User management service web api web api.
     *
     * @return the web api
     */
    @Bean
    @Qualifier("userManagementServiceWebClient")
    public WebClient userManagementServiceWebClient() {
        return WebClient.builder()
                .baseUrl(userAndGroupURI)
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                })
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(addSecurityHeader());
                    exchangeFilterFunctions.add(javaMicroserviceExceptionHandlingFunction());
                })
                .build();
    }

    /**
     * Elasticsearch service web api.
     *
     * @return the web api
     */
    @Bean
    @Qualifier("elasticsearchServiceWebClient")
    public WebClient elasticsearchServiceWebClient() {
        return WebClient.builder()
                .baseUrl(elasticsearchServiceURI)
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                })
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(addSecurityHeader());
                    exchangeFilterFunctions.add(javaMicroserviceExceptionHandlingFunction());
                })
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }

    private ExchangeFilterFunction addSecurityHeader() {
        return (request, next) -> {
            String bearerToken = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
            ClientRequest filtered = ClientRequest.from(request)
                    .header("Authorization", bearerToken)
                    .build();
            return next.exchange(filtered);
        };
    }

    private ExchangeFilterFunction javaMicroserviceExceptionHandlingFunction() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is4xxClientError() || clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            JavaApiError javaApiError = obtainSuitableJavaApiError(errorBody);
                            throw new CustomWebClientException(clientResponse.statusCode(), javaApiError);
                        });
            } else {
                return Mono.just(clientResponse);
            }
        });
    }

    private JavaApiError obtainSuitableJavaApiError(String errorBody) {
        if (errorBody == null || errorBody.isBlank()) {
            return JavaApiError.of("No specific message provided.");
        }
        try {
            return objectMapper.readValue(errorBody, JavaApiError.class);
        } catch (IOException e) {
            return JavaApiError.of("Could not obtain error message. Error body is: " + errorBody);
        }
    }
}


