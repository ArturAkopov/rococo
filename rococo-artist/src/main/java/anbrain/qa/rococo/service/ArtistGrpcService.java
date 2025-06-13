package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.ArtistEntity;
import anbrain.qa.rococo.data.repository.ArtistRepository;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.utils.GrpcArtistConverter;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class ArtistGrpcService extends ArtistServiceGrpc.ArtistServiceImplBase {

    private final ArtistRepository artistRepository;
    private final GrpcArtistConverter grpcArtistConverter;

    @Override
    public void getArtist(@Nonnull ArtistRequest request, @Nonnull StreamObserver<ArtistResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        ArtistEntity artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));

        responseObserver.onNext(grpcArtistConverter.toGrpcResponse(artist));
        responseObserver.onCompleted();
    }

    @Override
    public void getAllArtists(@Nonnull AllArtistsRequest request, @Nonnull StreamObserver<AllArtistsResponse> responseObserver) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<ArtistEntity> artistPage = artistRepository.findAll(pageable);

        AllArtistsResponse response = AllArtistsResponse.newBuilder()
                .addAllArtists(artistPage.getContent().stream()
                        .map(grpcArtistConverter::toGrpcResponse)
                        .toList())
                .setTotalCount((int) artistPage.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void searchArtistsByName(@Nonnull SearchArtistsRequest request, @Nonnull StreamObserver<AllArtistsResponse> responseObserver) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<ArtistEntity> artistPage = artistRepository.findByName(request.getName(), pageable);

        AllArtistsResponse response = AllArtistsResponse.newBuilder()
                .addAllArtists(artistPage.getContent().stream()
                        .map(grpcArtistConverter::toGrpcResponse)
                        .toList())
                .setTotalCount((int) artistPage.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createArtist(@Nonnull CreateArtistRequest request, @Nonnull StreamObserver<ArtistResponse> responseObserver) {
        ArtistEntity newArtist = new ArtistEntity();
        newArtist.setName(request.getName());
        newArtist.setBiography(request.getBiography());
        newArtist.setPhoto(request.getPhoto().getBytes(StandardCharsets.UTF_8));

        ArtistEntity savedArtist = artistRepository.save(newArtist);

        responseObserver.onNext(grpcArtistConverter.toGrpcResponse(savedArtist));
        responseObserver.onCompleted();
    }

    @Override
    public void updateArtist(@Nonnull UpdateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        ArtistEntity artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));

        artist.setName(request.getName());
        artist.setBiography(request.getBiography());
        if (!request.getPhoto().isEmpty()) {
            artist.setPhoto(request.getPhoto().getBytes(StandardCharsets.UTF_8));
        }

        ArtistEntity updatedArtist = artistRepository.save(artist);

        responseObserver.onNext(grpcArtistConverter.toGrpcResponse(updatedArtist));
        responseObserver.onCompleted();
    }
}