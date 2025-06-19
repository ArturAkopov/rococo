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
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class MuseumGrpcService extends MuseumServiceGrpc.MuseumServiceImplBase {

    private final MuseumDatabaseService museumDatabaseService;

    @Override
    public void getMuseum(@Nonnull MuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {
        log.debug("Начало обработки запроса getMuseum для ID: {}", request.getId());
            MuseumEntity entity = museumDatabaseService.findById(UUID.fromString(request.getId()));
            log.info("Успешно найден музей с ID: {}", request.getId());

            responseObserver.onNext(toGrpcResponse(entity));
            responseObserver.onCompleted();
            log.debug("Запрос getMuseum успешно завершен");
    }

    @Override
    public void getAllMuseums(@Nonnull AllMuseumsRequest request, @Nonnull StreamObserver<AllMuseumsResponse> responseObserver) {
        log.debug("Начало обработки запроса getAllMuseums. Страница: {}, Размер: {}",
                request.getPage(), request.getSize());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<MuseumEntity> page = museumDatabaseService.getAll(pageable);

        log.info("Возвращено {} музеев из {}", page.getContent().size(), page.getTotalElements());
        responseObserver.onNext(buildAllMuseumsResponse(page));
        responseObserver.onCompleted();
        log.debug("Запрос getAllMuseums успешно завершен");
    }

    @Override
    public void searchMuseumsByTitle(@Nonnull SearchMuseumsRequest request, @Nonnull StreamObserver<SearchMuseumsResponse> responseObserver) {
        log.debug("Начало поиска музеев по названию: '{}'", request.getTitle());

        if (request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Поисковый запрос не может быть пустым");
        }

        List<MuseumEntity> museums = museumDatabaseService.searchByTitle(request.getTitle());
        log.info("Найдено {} музеев по запросу '{}'", museums.size(), request.getTitle());

        responseObserver.onNext(buildSearchResponse(museums));
        responseObserver.onCompleted();
        log.debug("Поиск музеев успешно завершен");
    }

    @Override
    public void createMuseum(@Nonnull CreateMuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {
        log.debug("Начало создания музея с названием: '{}'", request.getTitle());

        if (request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Название музея обязательно для заполнения");
        }

        MuseumEntity created = museumDatabaseService.create(convertToJson(request));
        log.info("Успешно создан музей с ID: {}", created.getId());

        responseObserver.onNext(toGrpcResponse(created));
        responseObserver.onCompleted();
        log.debug("Создание музея успешно завершено");
    }

    @Override
    public void updateMuseum(@Nonnull UpdateMuseumRequest request, @Nonnull StreamObserver<MuseumResponse> responseObserver) {
        log.debug("Начало обновления музея с ID: {}", request.getId());

        if (request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Название музея обязательно для заполнения");
        }

        MuseumEntity updated = museumDatabaseService.update(convertToJson(request));
        log.info("Успешно обновлен музей с ID: {}", updated.getId());

        responseObserver.onNext(toGrpcResponse(updated));
        responseObserver.onCompleted();
        log.debug("Обновление музея успешно завершено");
    }

    @Nonnull
    private AllMuseumsResponse buildAllMuseumsResponse(@Nonnull Page<MuseumEntity> page) {
        return AllMuseumsResponse.newBuilder()
                .addAllMuseums(page.getContent().stream()
                        .map(this::toGrpcResponse)
                        .toList())
                .setTotalCount((int) page.getTotalElements())
                .build();
    }

    @Nonnull
    private SearchMuseumsResponse buildSearchResponse(@Nonnull List<MuseumEntity> museums) {
        return SearchMuseumsResponse.newBuilder()
                .addAllMuseums(museums.stream()
                        .map(this::toGrpcResponse)
                        .toList())
                .build();
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

    @Nonnull
    private MuseumJson convertToJson(@Nonnull CreateMuseumRequest request) {
        return new MuseumJson(
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
    }

    @Nonnull
    private MuseumJson convertToJson(@Nonnull UpdateMuseumRequest request) {
        return new MuseumJson(
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
    }
}