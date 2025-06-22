package anbrain.qa.rococo.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
@NoArgsConstructor
public class RococoServiceUnavailableException extends RuntimeException {
    public RococoServiceUnavailableException(String message) {
        super(message);
    }
}
