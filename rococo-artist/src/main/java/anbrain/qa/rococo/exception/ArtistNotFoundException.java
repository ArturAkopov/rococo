package anbrain.qa.rococo.exception;

import java.util.UUID;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(UUID id) {
        super("Artist with id " + id + " not found");
    }
}
