package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.MuseumEntity;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.GeoJson;
import anbrain.qa.rococo.model.MuseumJson;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class MuseumGrpcService extends MuseumServiceGrpc.MuseumServiceImplBase {

    private final MuseumDatabaseService museumDatabaseService;

    @Override
    public void getMuseum(@Nonnull MuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {

        MuseumJson museum = entityToJson(museumDatabaseService.findById(UUID.fromString(request.getId())));

        responseObserver.onNext(jsonToGrpcResponse(museum));
        responseObserver.onCompleted();
    }

    @Override
    public void getAllMuseums(@Nonnull AllMuseumsRequest request, @Nonnull StreamObserver<AllMuseumsResponse> responseObserver) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<MuseumJson> museumPage = museumDatabaseService.getAll(pageable).map(
                this::entityToJson
        );

        AllMuseumsResponse response = AllMuseumsResponse.newBuilder()
                .addAllMuseums(museumPage.getContent().stream()
                        .map(this::jsonToGrpcResponse)
                        .toList())
                .setTotalCount((int) museumPage.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void searchMuseumsByTitle(@Nonnull SearchMuseumsRequest request, @Nonnull StreamObserver<SearchMuseumsResponse> responseObserver) {

        List<MuseumResponse> museums = museumDatabaseService.searchByTitle(request.getTitle()).stream()
                .map(this::entityToJson)
                .map(this::jsonToGrpcResponse)
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

            MuseumJson created = entityToJson(museumDatabaseService.create(museum));

            responseObserver.onNext(jsonToGrpcResponse(created));
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

        MuseumJson updated = entityToJson(museumDatabaseService.update(museum));

        responseObserver.onNext(jsonToGrpcResponse(updated));
        responseObserver.onCompleted();
    }

    @Nonnull
    private MuseumResponse jsonToGrpcResponse(@Nonnull MuseumJson museum) {
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


    @Nonnull
    private MuseumJson entityToJson(@Nonnull MuseumEntity entity) {
        return new MuseumJson(
                entity.getId() != null ? entity.getId() : null,
                entity.getTitle(),
                entity.getDescription(),
                entity.getPhoto() != null ? new String(entity.getPhoto()) : null,
                new GeoJson(
                        entity.getCity(),
                        new CountryJson(
                                entity.getCountry().getId(),
                                entity.getCountry().getName() != null ? entity.getCountry().getName() : null
                        )
                )
        );
    }
}