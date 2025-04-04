package cz.cyberrange.platform.training.feedback.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The requested resource was not found")
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }

    public ResourceNotFoundException(Throwable ex) {
        super(ex);
    }

}
