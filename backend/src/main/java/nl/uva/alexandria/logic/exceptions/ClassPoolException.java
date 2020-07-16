package nl.uva.alexandria.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ClassPoolException extends RuntimeException {
    public ClassPoolException(String message) {
        super(message);
    }
}
