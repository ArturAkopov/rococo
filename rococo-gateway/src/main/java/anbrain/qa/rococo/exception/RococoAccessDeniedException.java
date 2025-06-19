package anbrain.qa.rococo.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
@NoArgsConstructor
public class RococoAccessDeniedException extends RuntimeException {
    public RococoAccessDeniedException(String message) {
        super(message);
    }
}
