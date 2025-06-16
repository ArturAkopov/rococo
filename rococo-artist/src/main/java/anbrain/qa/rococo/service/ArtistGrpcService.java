package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.ArtistEntity;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.utils.GrpcArtistConverter;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class ArtistGrpcService extends ArtistServiceGrpc.ArtistServiceImplBase {

    private final ArtistDatabaseService artistDatabaseService;
    private final GrpcArtistConverter grpcArtistConverter;

    @Override
    public void getArtist(@Nonnull ArtistRequest request, @Nonnull StreamObserver<ArtistResponse> responseObserver) {

        ArtistEntity artist = artistDatabaseService.getArtist(UUID.fromString(request.getId()));

        responseObserver.onNext(grpcArtistConverter.entityToGrpcResponse(artist));
        responseObserver.onCompleted();
    }

    @Override
    public void getAllArtists(@Nonnull AllArtistsRequest request, @Nonnull StreamObserver<AllArtistsResponse> responseObserver) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<ArtistEntity> artistPage = artistDatabaseService.getAllArtists(pageable);

        AllArtistsResponse response = grpcArtistConverter.pageToGrpcResponse(artistPage);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void searchArtistsByName(@Nonnull SearchArtistsRequest request, @Nonnull StreamObserver<AllArtistsResponse> responseObserver) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<ArtistEntity> artistPage = artistDatabaseService.searchArtistsByName(request.getName(), pageable);

        AllArtistsResponse response = grpcArtistConverter.pageToGrpcResponse(artistPage);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createArtist(@Nonnull CreateArtistRequest request, @Nonnull StreamObserver<ArtistResponse> responseObserver) {

       final ArtistEntity savedArtist = artistDatabaseService.createArtist(
                request.getName(),
                request.getBiography(),
                request.getPhoto().getBytes(StandardCharsets.UTF_8)
        );

        responseObserver.onNext(grpcArtistConverter.entityToGrpcResponse(savedArtist));
        responseObserver.onCompleted();
    }

    @Override
    public void updateArtist(@Nonnull UpdateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {

        final ArtistEntity updatedArtist = artistDatabaseService.updateArtist(
                UUID.fromString(request.getId()),
                request.getName(),
                request.getBiography(),
                request.getPhoto().isEmpty() ? null : request.getPhoto().getBytes(StandardCharsets.UTF_8)
        );

        responseObserver.onNext(grpcArtistConverter.entityToGrpcResponse(updatedArtist));
        responseObserver.onCompleted();
    }
}