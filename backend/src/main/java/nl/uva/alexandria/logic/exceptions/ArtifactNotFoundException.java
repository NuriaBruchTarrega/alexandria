package nl.uva.alexandria.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ArtifactNotFoundException extends RuntimeException {
    public ArtifactNotFoundException(String message) {
        super(message);
    }
}
