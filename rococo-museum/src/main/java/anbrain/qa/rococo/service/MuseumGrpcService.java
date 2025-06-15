package anbrain.qa.rococo.service;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.GeoJson;
import anbrain.qa.rococo.model.MuseumJson;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class MuseumGrpcService extends MuseumServiceGrpc.MuseumServiceImplBase {

    private final MuseumService museumService;

    @Override
    public void getMuseum(@Nonnull MuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {
        MuseumJson museum = museumService.findById(UUID.fromString(request.getId()));
        responseObserver.onNext(toGrpcResponse(museum));
        responseObserver.onCompleted();
    }

    @Override
    public void getAllMuseums(@Nonnull AllMuseumsRequest request, @Nonnull StreamObserver<AllMuseumsResponse> responseObserver) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<MuseumJson> museumPage = museumService.getAll(pageable);

        AllMuseumsResponse response = AllMuseumsResponse.newBuilder()
                .addAllMuseums(museumPage.getContent().stream()
                        .map(this::toGrpcResponse)
                        .toList())
                .setTotalCount((int) museumPage.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void searchMuseumsByTitle(@Nonnull SearchMuseumsRequest request, @Nonnull StreamObserver<SearchMuseumsResponse> responseObserver) {
        List<MuseumResponse> museums = museumService.searchByTitle(request.getTitle()).stream()
                .map(this::toGrpcResponse)
                .toList();

        SearchMuseumsResponse response = SearchMuseumsResponse.newBuilder()
                .addAllMuseums(museums)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createMuseum(@Nonnull CreateMuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {
        try {

            MuseumJson museum = new MuseumJson(
                    null,
                    request.getTitle(),
                    request.getDescription(),
                    request.getPhoto(),
                    new GeoJson(
                            request.getGeo().getCity(),
                            new CountryJson(
                                    UUID.fromString(request.getGeo().getCountry().getId()),
                                    null
                            )
                    )
            );

            MuseumJson created = museumService.create(museum);
            responseObserver.onNext(toGrpcResponse(created));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error creating museum: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateMuseum(@Nonnull UpdateMuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {
        MuseumJson museum = new MuseumJson(
                UUID.fromString(request.getId()),
                request.getTitle(),
                request.getDescription(),
                request.getPhoto(),
                new GeoJson(
                        request.getGeo().getCity(),
                        new CountryJson(
                                UUID.fromString(request.getGeo().getCountry().getId()),
                                request.getGeo().getCountry().getName()
                        )
                )
        );

        MuseumJson updated = museumService.update(museum);
        responseObserver.onNext(toGrpcResponse(updated));
        responseObserver.onCompleted();
    }

    @Nonnull
    private MuseumResponse toGrpcResponse(@Nonnull MuseumJson museum) {
        Country.Builder countryBuilder = Country.newBuilder()
                .setId(museum.geo().country().id().toString());

        if (museum.geo().country().name() != null) {
            countryBuilder.setName(museum.geo().country().name());
        }

        return MuseumResponse.newBuilder()
                .setId(museum.id().toString())
                .setTitle(museum.title())
                .setDescription(museum.description())
                .setPhoto(museum.photo())
                .setGeo(Geo.newBuilder()
                        .setCity(museum.geo().city())
                        .setCountry(countryBuilder)
                        .build())
                .build();
    }
}