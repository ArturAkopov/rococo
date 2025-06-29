package anbrain.qa.rococo.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@NoArgsConstructor

public class RococoNotFoundException extends RuntimeException {

    public RococoNotFoundException(String message) {
        super(message);
    }
}