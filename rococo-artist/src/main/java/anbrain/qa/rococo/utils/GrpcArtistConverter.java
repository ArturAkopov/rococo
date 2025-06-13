package anbrain.qa.rococo.utils;

import anbrain.qa.rococo.data.ArtistEntity;
import anbrain.qa.rococo.grpc.ArtistResponse;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class GrpcArtistConverter {

    public ArtistResponse toGrpcResponse(@Nonnull ArtistEntity entity) {
        return ArtistResponse.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName())
                .setBiography(entity.getBiography())
                .setPhoto(new String(entity.getPhoto()))
                .build();
    }
}
