package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.MuseumEntity;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.GeoJson;
import anbrain.qa.rococo.model.MuseumJson;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class MuseumGrpcService extends MuseumServiceGrpc.MuseumServiceImplBase {

    private final MuseumDatabaseService museumDatabaseService;

    @Override
    public void getMuseum(@Nonnull MuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {
        MuseumEntity entity = museumDatabaseService.findById(UUID.fromString(request.getId()));
        responseObserver.onNext(toGrpcResponse(entity));
        responseObserver.onCompleted();
    }

    @Override
    public void getAllMuseums(@Nonnull AllMuseumsRequest request, @Nonnull StreamObserver<AllMuseumsResponse> responseObserver) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<MuseumEntity> page = museumDatabaseService.getAll(pageable);

        responseObserver.onNext(AllMuseumsResponse.newBuilder()
                .addAllMuseums(page.getContent().stream()
                        .map(this::toGrpcResponse)
                        .toList())
                .setTotalCount((int) page.getTotalElements())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void searchMuseumsByTitle(@Nonnull SearchMuseumsRequest request, @Nonnull StreamObserver<SearchMuseumsResponse> responseObserver) {
        if (request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Поисковый запрос не может быть пустым");
        }

        List<MuseumEntity> museums = museumDatabaseService.searchByTitle(request.getTitle());
        responseObserver.onNext(SearchMuseumsResponse.newBuilder()
                .addAllMuseums(museums.stream()
                        .map(this::toGrpcResponse)
                        .toList())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void createMuseum(@Nonnull CreateMuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {
        if (request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Название музея обязательно для заполнения");
        }

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

        MuseumEntity created = museumDatabaseService.create(museum);
        responseObserver.onNext(toGrpcResponse(created));
        responseObserver.onCompleted();
    }

    @Override
    public void updateMuseum(@Nonnull UpdateMuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {
        if (request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Название музея обязательно для заполнения");
        }

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

        MuseumEntity updated = museumDatabaseService.update(museum);
        responseObserver.onNext(toGrpcResponse(updated));
        responseObserver.onCompleted();
    }

    @Nonnull
    private MuseumResponse toGrpcResponse(@Nonnull MuseumEntity entity) {
        return MuseumResponse.newBuilder()
                .setId(entity.getId().toString())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setPhoto(entity.getPhoto() != null ? new String(entity.getPhoto()) : "")
                .setGeo(Geo.newBuilder()
                        .setCity(entity.getCity())
                        .setCountry(Country.newBuilder()
                                .setId(entity.getCountry().getId().toString())
                                .setName(entity.getCountry().getName())
                                .build())
                        .build())
                .build();
    }
}