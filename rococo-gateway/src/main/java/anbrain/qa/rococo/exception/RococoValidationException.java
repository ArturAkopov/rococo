package anbrain.qa.rococo.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@NoArgsConstructor
public class RococoValidationException extends RuntimeException {
    public RococoValidationException(String message) {
        super(message);
    }
}
