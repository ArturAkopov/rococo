package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.ArtistJson;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class ArtistGrpcClient {

    @GrpcClient("rococo-artist")
    private ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    public ArtistJson getArtist(@Nonnull UUID id) {
        try {
            log.info("Запрос художника с ID: {}", id);
            ArtistResponse response = artistStub.getArtist(
                    ArtistRequest.newBuilder()
                            .setId(id.toString())
                            .build());
            log.info("Художник с ID {} успешно получен", id);
            return toArtistJson(response);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при получении художника с ID {}: {}", id, e.getStatus().getDescription());
            throw handleGrpcException(e, "Художник", id.toString());
        }
    }

    public Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable) {
        try {
            log.info("Запрос всех художников, страница {}, размер {}", pageable.getPageNumber(), pageable.getPageSize());
            AllArtistsResponse response = artistStub.getAllArtists(
                    AllArtistsRequest.newBuilder()
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<ArtistJson> artists = response.getArtistsList().stream()
                    .map(this::toArtistJson)
                    .collect(Collectors.toList());

            log.info("Получено {} художников из {}", artists.size(), response.getTotalCount());
            return new PageImpl<>(
                    artists,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при получении списка художников: {}", e.getStatus().getDescription());
            throw handleGrpcException(e, "Список художников", "страница " + pageable.getPageNumber());
        }
    }

    public Page<ArtistJson> searchArtistsByName(String name, @Nonnull Pageable pageable) {
        try {
            log.info("Поиск художников по имени '{}', страница {}, размер {}", name, pageable.getPageNumber(), pageable.getPageSize());
            AllArtistsResponse response = artistStub.searchArtistsByName(
                    SearchArtistsRequest.newBuilder()
                            .setName(name)
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<ArtistJson> artists = response.getArtistsList().stream()
                    .map(this::toArtistJson)
                    .collect(Collectors.toList());

            log.info("Найдено {} художников по запросу '{}'", artists.size(), name);
            return new PageImpl<>(
                    artists,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при поиске художников по имени '{}': {}", name, e.getStatus().getDescription());
            throw handleGrpcException(e, "Поиск художников", name);
        }
    }

    public ArtistJson createArtist(@Nonnull ArtistJson artist) {
        if (artist.name()==null||artist.name().isEmpty())
            throw new IllegalArgumentException("Название музея не может быть "+artist.name());
        try {
            log.info("Создание нового художника с именем '{}'", artist.name());
            ArtistResponse response = artistStub.createArtist(
                    CreateArtistRequest.newBuilder()
                            .setName(artist.name())
                            .setBiography(artist.biography())
                            .setPhoto(artist.photo())
                            .build());
            log.info("Художник '{}' успешно создан с ID {}", artist.name(), response.getId());
            return toArtistJson(response);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при создании художника '{}': {}", artist.name(), e.getStatus().getDescription());
            throw handleGrpcException(e, "Создание художника", artist.name());
        }
    }

    public ArtistJson updateArtist(@Nonnull ArtistJson artist) {
        try {
            log.info("Обновление художника с ID {}", artist.id());
            ArtistResponse response = artistStub.updateArtist(
                    UpdateArtistRequest.newBuilder()
                            .setId(artist.id().toString())
                            .setName(artist.name())
                            .setBiography(artist.biography())
                            .setPhoto(artist.photo())
                            .build());
            log.info("Художник с ID {} успешно обновлен", artist.id());
            return toArtistJson(response);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при обновлении художника с ID {}: {}", artist.id(), e.getStatus().getDescription());
            throw handleGrpcException(e, "Обновление художника", artist.id().toString());
        }
    }

    @Nonnull
    public ArtistJson toArtistJson(@Nonnull ArtistResponse response) {
        return new ArtistJson(
                UUID.fromString(response.getId()),
                response.getName(),
                response.getBiography(),
                response.getPhoto()
        );
    }
}