package anbrain.qa.rococo.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@NoArgsConstructor
public class RococoConflictException extends RuntimeException {

    public RococoConflictException(String message) {
        super(message);
    }
}
