package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.PaintingEntity;
import anbrain.qa.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class PaintingGrpcService extends PaintingServiceGrpc.PaintingServiceImplBase {

    private final PaintingDatabaseService paintingDatabaseService;

    @Override
    public void getPainting(@Nonnull PaintingRequest request, @Nonnull StreamObserver<PaintingResponse> responseObserver) {
        log.debug("Начало обработки запроса getPainting для ID: {}", request.getId());
            UUID id = UUID.fromString(request.getId());
            PaintingEntity painting = paintingDatabaseService.getPainting(id);
            log.info("Успешно найдена картина с ID: {}", request.getId());

            responseObserver.onNext(buildPaintingResponse(painting));
            responseObserver.onCompleted();
            log.debug("Запрос getPainting успешно завершен");
    }

    @Override
    public void getAllPaintings(@Nonnull AllPaintingsRequest request, @Nonnull StreamObserver<AllPaintingsResponse> responseObserver) {
        log.debug("Начало обработки запроса getAllPaintings. Страница: {}, Размер: {}",
                request.getPage(), request.getSize());

            Page<PaintingEntity> paintings = paintingDatabaseService.getAllPaintings(
                    PageRequest.of(request.getPage(), request.getSize()));

            log.info("Возвращено {} картин из {}", paintings.getContent().size(), paintings.getTotalElements());
            responseObserver.onNext(buildAllPaintingsResponse(paintings));
            responseObserver.onCompleted();
            log.debug("Запрос getAllPaintings успешно завершен");
    }

    @Override
    public void getPaintingsByArtist(@Nonnull PaintingsByArtistRequest request, @Nonnull StreamObserver<AllPaintingsResponse> responseObserver) {
        log.debug("Начало обработки запроса getPaintingsByArtist. ArtistID: {}, Страница: {}, Размер: {}",
                request.getArtistId(), request.getPage(), request.getSize());

            UUID artistId = UUID.fromString(request.getArtistId());
            Page<PaintingEntity> paintings = paintingDatabaseService.getPaintingsByArtist(
                    artistId,
                    PageRequest.of(request.getPage(), request.getSize()));

            log.info("Найдено {} картин художника {}", paintings.getContent().size(), request.getArtistId());
            responseObserver.onNext(buildAllPaintingsResponse(paintings));
            responseObserver.onCompleted();
            log.debug("Запрос getPaintingsByArtist успешно завершен");
    }

    @Override
    public void createPainting(@Nonnull CreatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        log.debug("Начало создания картины с названием: '{}'", request.getTitle());

            if (request.getTitle().isBlank()) {
                log.warn("Попытка создания картины без названия");
                throw new IllegalArgumentException("Название картины обязательно для заполнения");
            }

            PaintingEntity painting = new PaintingEntity();
            painting.setTitle(request.getTitle());
            painting.setDescription(request.getDescription());
            painting.setContent(request.getContent().getBytes());
            painting.setArtistId(UUID.fromString(request.getArtistId()));
            painting.setMuseumId(UUID.fromString(request.getMuseumId()));

            PaintingEntity savedPainting = paintingDatabaseService.createPainting(painting);
            log.info("Успешно создана картина с ID: {}", savedPainting.getId());

            responseObserver.onNext(buildPaintingResponse(savedPainting));
            responseObserver.onCompleted();
            log.debug("Создание картины успешно завершено");
    }

    @Override
    public void updatePainting(@Nonnull UpdatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        log.debug("Начало обновления картины с ID: {}", request.getId());

            if (request.getTitle().isBlank()) {
                log.warn("Попытка обновления картины без названия");
                throw new IllegalArgumentException("Название картины обязательно для заполнения");
            }

            UUID id = UUID.fromString(request.getId());
            PaintingEntity painting = paintingDatabaseService.getPainting(id);

            painting.setTitle(request.getTitle());
            painting.setDescription(request.getDescription());
            painting.setContent(request.getContent().getBytes());
            painting.setArtistId(UUID.fromString(request.getArtistId()));
            painting.setMuseumId(UUID.fromString(request.getMuseumId()));

            PaintingEntity updatedPainting = paintingDatabaseService.updatePainting(painting);
            log.info("Успешно обновлена картина с ID: {}", updatedPainting.getId());

            responseObserver.onNext(buildPaintingResponse(updatedPainting));
            responseObserver.onCompleted();
            log.debug("Обновление картины успешно завершено");
    }

    @Nonnull
    private AllPaintingsResponse buildAllPaintingsResponse(@Nonnull Page<PaintingEntity> paintings) {
        AllPaintingsResponse.Builder builder = AllPaintingsResponse.newBuilder()
                .setTotalCount((int) paintings.getTotalElements());

        paintings.getContent().forEach(painting -> {
            builder.addPaintings(buildPaintingResponse(painting));
        });

        return builder.build();
    }

    @Nonnull
    private PaintingResponse buildPaintingResponse(@Nonnull PaintingEntity painting) {
        return PaintingResponse.newBuilder()
                .setId(painting.getId().toString())
                .setTitle(painting.getTitle())
                .setDescription(painting.getDescription())
                .setContent(new String(painting.getContent()))
                .setMuseumId(painting.getMuseumId().toString())
                .setArtistId(painting.getArtistId().toString())
                .build();
    }
}