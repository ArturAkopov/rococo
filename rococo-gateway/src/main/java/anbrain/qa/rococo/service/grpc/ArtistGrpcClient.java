package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.ArtistJson;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static anbrain.qa.rococo.exception.GrpcExceptionHandler.handleGrpcException;

@Service
public class ArtistGrpcClient {

    @GrpcClient("rococo-artist")
    private ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    public ArtistJson getArtist(@Nonnull UUID id) {
        try {
            ArtistResponse response = artistStub.getArtist(
                    ArtistRequest.newBuilder()
                            .setId(id.toString())
                            .build());
            return toArtistJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Artist", id.toString());
        }
    }

    public Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable) {
        try {
            AllArtistsResponse response = artistStub.getAllArtists(
                    AllArtistsRequest.newBuilder()
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<ArtistJson> artists = response.getArtistsList().stream()
                    .map(this::toArtistJson)
                    .collect(Collectors.toList());

            return new PageImpl<>(
                    artists,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e,"Artist", "get all");
        }
    }

    public Page<ArtistJson> searchArtistsByName(String name, @Nonnull Pageable pageable) {
        try {
            AllArtistsResponse response = artistStub.searchArtistsByName(
                    SearchArtistsRequest.newBuilder()
                            .setName(name)
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<ArtistJson> artists = response.getArtistsList().stream()
                    .map(this::toArtistJson)
                    .collect(Collectors.toList());

            return new PageImpl<>(
                    artists,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Artist", "search by name");
        }
    }

    public ArtistJson createArtist(@Nonnull ArtistJson artist) {
        try {
            ArtistResponse response = artistStub.createArtist(
                    CreateArtistRequest.newBuilder()
                            .setName(artist.name())
                            .setBiography(artist.biography())
                            .setPhoto(artist.photo())
                            .build());
            return toArtistJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Artist", "creation request");
        }
    }

    public ArtistJson updateArtist(@Nonnull ArtistJson artist) {
        try {
            ArtistResponse response = artistStub.updateArtist(
                    UpdateArtistRequest.newBuilder()
                            .setId(artist.id().toString())
                            .setName(artist.name())
                            .setBiography(artist.biography())
                            .setPhoto(artist.photo())
                            .build());
            return toArtistJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Artist", artist.id().toString());
        }
    }

    @Nonnull
    private ArtistJson toArtistJson(@Nonnull ArtistResponse response) {
        return new ArtistJson(
                UUID.fromString(response.getId()),
                response.getName(),
                response.getBiography(),
                response.getPhoto()
        );
    }

}
