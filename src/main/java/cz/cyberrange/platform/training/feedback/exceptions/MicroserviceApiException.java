package cz.cyberrange.platform.training.feedback.exceptions;

import cz.cyberrange.platform.training.feedback.exceptions.errors.ApiSubError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason = "Error when calling external service API")
public class MicroserviceApiException extends RuntimeException {
    private final HttpStatus statusCode;
    private final ApiSubError apiSubError;

    public MicroserviceApiException(String message, HttpStatus statusCode, ApiSubError apiSubError) {
        super(message + " " + apiSubError.getMessage());
        this.statusCode = statusCode;
        this.apiSubError = apiSubError;
    }

    public MicroserviceApiException(HttpStatus statusCode, ApiSubError apiSubError) {
        this("Error when calling external microservice.", statusCode, apiSubError);
    }

    public MicroserviceApiException(String message, CustomWebClientException customWebClientException) {
        this(message, customWebClientException.getStatusCode(), customWebClientException.getApiSubError());
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public ApiSubError getApiSubError() {
        return apiSubError;
    }
}
