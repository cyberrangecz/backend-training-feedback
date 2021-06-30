package cz.muni.ics.kypo.training.feedback.exceptions;

import cz.muni.ics.kypo.training.feedback.exceptions.errors.ApiSubError;
import org.springframework.http.HttpStatus;

/**
 * The type Rest template exception.
 */
public class CustomWebClientException extends RuntimeException {
    private final HttpStatus statusCode;
    private final ApiSubError apiSubError;

    /**
     * Instantiates a new Rest template exception.
     *
     * @param apiSubError detailed information about error.
     */
    public CustomWebClientException(HttpStatus httpStatus, ApiSubError apiSubError) {
        super();
        this.apiSubError = apiSubError;
        this.statusCode = httpStatus;
    }

    /**
     * Gets detailed information about error.
     *
     * @return detailed information about error
     */
    public ApiSubError getApiSubError() {
        return apiSubError;
    }

    /**
     * Gets status code.
     *
     * @return the status code
     */
    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
