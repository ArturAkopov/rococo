package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.*;
import anbrain.qa.rococo.model.page.RestPage;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static anbrain.qa.rococo.exception.GrpcExceptionHandler.handleGrpcException;

@Slf4j
@Service
public class PaintingGrpcClient {

    private final ArtistGrpcClient artistGrpcClient;
    private final MuseumGrpcClient museumGrpcClient;

    @Autowired
    public PaintingGrpcClient(ArtistGrpcClient artistGrpcClient, MuseumGrpcClient museumGrpcClient) {
        this.artistGrpcClient = artistGrpcClient;
        this.museumGrpcClient = museumGrpcClient;
    }

    @GrpcClient("rococo-painting")
    private PaintingServiceGrpc.PaintingServiceBlockingStub paintingStub;

    public PaintingJson getPainting(@Nonnull UUID id) {
        try {
            log.info("Запрос картины с ID: {}", id);
            PaintingResponse response = paintingStub.getPainting(
                    PaintingRequest.newBuilder()
                            .setId(id.toString())
                            .build());
            final ArtistJson artistJson = artistGrpcClient.getArtist(UUID.fromString(response.getArtistId()));
            final MuseumJson museumJson = museumGrpcClient.getMuseum(UUID.fromString(response.getMuseumId()));

            log.info("Картина с ID {} успешно получена", id);
            return toPaintingJson(response, museumJson, artistJson);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при получении картины с ID {}: {}", id, e.getStatus().getDescription());
            throw handleGrpcException(e, "Картина", id.toString());
        }
    }

    public RestPage<PaintingJson> getAllPaintings(@Nonnull Pageable pageable) {
        try {
            log.info("Запрос всех картин, страница {}, размер {}", pageable.getPageNumber(), pageable.getPageSize());
            AllPaintingsResponse response = paintingStub.getAllPaintings(
                    AllPaintingsRequest.newBuilder()
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<PaintingJson> paintings = response.getPaintingsList().parallelStream()
                    .map(p -> {
                        try {
                            ArtistJson artistJson = artistGrpcClient.getArtist(UUID.fromString(p.getArtistId()));
                            MuseumJson museumJson = museumGrpcClient.getMuseum(UUID.fromString(p.getMuseumId()));
                            return toPaintingJson(p, museumJson, artistJson);
                        } catch (Exception e) {
                            log.error("Ошибка при загрузке зависимостей для картины {}: {}", p.getId(), e.getMessage());
                            throw new RuntimeException(String.format(
                                    "Не удалось загрузить зависимости для картины %s: %s",
                                    p.getId(),
                                    e.getMessage()
                            ), e);
                        }
                    })
                    .collect(Collectors.toList());

            log.info("Получено {} картин из {}", paintings.size(), response.getTotalCount());
            return new RestPage<>(
                    paintings,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при получении списка картин: {}", e.getStatus().getDescription());
            throw handleGrpcException(e, "Картины", "страница " + pageable.getPageNumber());
        }
    }

    public RestPage<PaintingJson> getPaintingsByArtist(@Nonnull UUID artistId, @Nonnull Pageable pageable) {
        try {
            log.info("Запрос картин художника {}, страница {}", artistId, pageable.getPageNumber());
            AllPaintingsResponse response = paintingStub.getPaintingsByArtist(
                    PaintingsByArtistRequest.newBuilder()
                            .setArtistId(artistId.toString())
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<PaintingJson> paintings = response.getPaintingsList().parallelStream()
                    .map(p -> {
                        try {
                            ArtistJson artistJson = artistGrpcClient.getArtist(UUID.fromString(p.getArtistId()));
                            MuseumJson museumJson = museumGrpcClient.getMuseum(UUID.fromString(p.getMuseumId()));
                            return toPaintingJson(p, museumJson, artistJson);
                        } catch (Exception e) {
                            log.error("Ошибка при загрузке зависимостей для картины {}: {}", p.getId(), e.getMessage());
                            throw new RuntimeException(String.format(
                                    "Не удалось загрузить зависимости для картины %s: %s",
                                    p.getId(),
                                    e.getMessage()
                            ), e);
                        }
                    })
                    .collect(Collectors.toList());

            log.info("Получено {} картин художника {}", paintings.size(), artistId);
            return new RestPage<>(
                    paintings,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при получении картин художника {}: {}", artistId, e.getStatus().getDescription());
            throw handleGrpcException(e, "Картины художника", artistId.toString());
        }
    }

    public PaintingJson createPainting(@Nonnull PaintingJson painting) {
        try {
            log.info("Создание новой картины с названием '{}'", painting.title());
            PaintingResponse response = paintingStub.createPainting(
                    CreatePaintingRequest.newBuilder()
                            .setTitle(painting.title())
                            .setDescription(painting.description())
                            .setContent(painting.content())
                            .setMuseumId(painting.museum().id().toString())
                            .setArtistId(painting.artist().id().toString())
                            .build());
            ArtistJson artistJson = artistGrpcClient.getArtist(UUID.fromString(response.getArtistId()));
            MuseumJson museumJson = museumGrpcClient.getMuseum(UUID.fromString(response.getMuseumId()));

            log.info("Картина '{}' успешно создана с ID {}", painting.title(), response.getId());
            return toPaintingJson(response, museumJson, artistJson);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при создании картины '{}': {}", painting.title(), e.getStatus().getDescription());
            throw handleGrpcException(e, "Создание картины", painting.title());
        }
    }

    public PaintingJson updatePainting(@Nonnull PaintingJson painting) {
        try {
            log.info("Обновление картины с ID {}", painting.id());
            PaintingResponse response = paintingStub.updatePainting(
                    UpdatePaintingRequest.newBuilder()
                            .setId(painting.id().toString())
                            .setTitle(painting.title())
                            .setDescription(painting.description())
                            .setContent(painting.content())
                            .setMuseumId(painting.museum().id().toString())
                            .setArtistId(painting.artist().id().toString())
                            .build());
            ArtistJson artistJson = artistGrpcClient.getArtist(UUID.fromString(response.getArtistId()));
            MuseumJson museumJson = museumGrpcClient.getMuseum(UUID.fromString(response.getMuseumId()));

            log.info("Картина с ID {} успешно обновлена", painting.id());
            return toPaintingJson(response, museumJson, artistJson);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при обновлении картины с ID {}: {}", painting.id(), e.getStatus().getDescription());
            throw handleGrpcException(e, "Обновление картины", painting.id().toString());
        }
    }

    @Nonnull
    private PaintingJson toPaintingJson(
            @Nonnull PaintingResponse response,
            @Nonnull MuseumJson museumJson,
            @Nonnull ArtistJson artistJson) {
        return new PaintingJson(
                UUID.fromString(response.getId()),
                response.getTitle(),
                response.getDescription(),
                response.getContent(),
                museumJson,
                artistJson
        );
    }
}