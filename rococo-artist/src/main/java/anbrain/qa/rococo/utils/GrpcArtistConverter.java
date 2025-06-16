package anbrain.qa.rococo.utils;

import anbrain.qa.rococo.data.ArtistEntity;
import anbrain.qa.rococo.grpc.AllArtistsResponse;
import anbrain.qa.rococo.grpc.ArtistResponse;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class GrpcArtistConverter {

    public ArtistResponse entityToGrpcResponse(@Nonnull ArtistEntity entity) {
        return ArtistResponse.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName())
                .setBiography(entity.getBiography())
                .setPhoto(new String(entity.getPhoto()))
                .build();
    }

    public AllArtistsResponse pageToGrpcResponse(@Nonnull Page<ArtistEntity> artistPage) {
        return AllArtistsResponse.newBuilder()
                .addAllArtists(artistPage.getContent().stream()
                        .map(this::entityToGrpcResponse)
                        .toList())
                .setTotalCount((int) artistPage.getTotalElements())
                .build();
    }
}
