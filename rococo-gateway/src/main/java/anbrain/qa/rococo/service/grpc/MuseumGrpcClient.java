package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.GeoJson;
import anbrain.qa.rococo.model.MuseumJson;
import anbrain.qa.rococo.model.page.RestPage;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static anbrain.qa.rococo.exception.GrpcExceptionHandler.handleGrpcException;

@Slf4j
@Service
public class MuseumGrpcClient {

    @GrpcClient("rococo-museum")
    private MuseumServiceGrpc.MuseumServiceBlockingStub museumStub;

    public MuseumJson getMuseum(@Nonnull UUID id) {
        try {
            log.info("Запрос музея с ID: {}", id);
            MuseumResponse response = museumStub.getMuseum(
                    MuseumRequest.newBuilder()
                            .setId(id.toString())
                            .build());
            log.info("Музей с ID {} успешно получен", id);
            return toMuseumJson(response);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при получении музея с ID {}: {}", id, e.getStatus().getDescription());
            throw handleGrpcException(e, "Музей", id.toString());
        }
    }

    public RestPage<MuseumJson> getAllMuseums(@Nonnull Pageable pageable) {
        try {
            log.info("Запрос всех музеев, страница {}, размер {}", pageable.getPageNumber(), pageable.getPageSize());
            AllMuseumsResponse response = museumStub.getAllMuseums(
                    AllMuseumsRequest.newBuilder()
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<MuseumJson> museums = response.getMuseumsList().stream()
                    .map(this::toMuseumJson)
                    .collect(Collectors.toList());

            log.info("Получено {} музеев из {}", museums.size(), response.getTotalCount());
            return new RestPage<>(
                    museums,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при получении списка музеев: {}", e.getStatus().getDescription());
            throw handleGrpcException(e, "Музеи", "страница " + pageable.getPageNumber());
        }
    }

    public List<MuseumJson> searchMuseumsByTitle(String title) {
        try {
            log.info("Поиск музеев по названию: '{}'", title);
            SearchMuseumsResponse response = museumStub.searchMuseumsByTitle(
                    SearchMuseumsRequest.newBuilder()
                            .setTitle(title)
                            .build());

            List<MuseumJson> museums = response.getMuseumsList().stream()
                    .map(this::toMuseumJson)
                    .collect(Collectors.toList());

            log.info("Найдено {} музеев по запросу '{}'", museums.size(), title);
            return museums;
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при поиске музеев по названию '{}': {}", title, e.getStatus().getDescription());
            throw handleGrpcException(e, "Поиск музеев", title);
        }
    }

    public MuseumJson createMuseum(@Nonnull MuseumJson museum) {
        if (museum.title()==null||museum.title().isEmpty()) throw new IllegalArgumentException("Название музея не может быть "+museum.title());
        try {
            log.info("Создание нового музея с названием '{}'", museum.title());
            MuseumResponse response = museumStub.createMuseum(
                    CreateMuseumRequest.newBuilder()
                            .setTitle(museum.title())
                            .setDescription(museum.description())
                            .setPhoto(museum.photo())
                            .setGeo(toGrpcGeo(museum.geo()))
                            .build());
            log.info("Музей '{}' успешно создан с ID {}", museum.title(), response.getId());
            return toMuseumJson(response);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при создании музея '{}': {}", museum.title(), e.getStatus().getDescription());
            throw handleGrpcException(e, "Создание музея", museum.title());
        }
    }

    public MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
        try {
            log.info("Обновление музея с ID {}", museum.id());
            MuseumResponse response = museumStub.updateMuseum(
                    UpdateMuseumRequest.newBuilder()
                            .setId(museum.id().toString())
                            .setTitle(museum.title())
                            .setDescription(museum.description())
                            .setPhoto(museum.photo())
                            .setGeo(toGrpcGeo(museum.geo()))
                            .build());
            log.info("Музей с ID {} успешно обновлен", museum.id());
            return toMuseumJson(response);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при обновлении музея с ID {}: {}", museum.id(), e.getStatus().getDescription());
            throw handleGrpcException(e, "Обновление музея", museum.id().toString());
        }
    }

    @Nonnull
    public Geo toGrpcGeo(@Nonnull GeoJson geo) {
        Country.Builder countryBuilder = Country.newBuilder()
                .setId(String.valueOf(geo.country().id()));

        Optional.ofNullable(geo.country().name()).ifPresent(countryBuilder::setName);

        return Geo.newBuilder()
                .setCity(geo.city())
                .setCountry(countryBuilder.build())
                .build();
    }

    @Nonnull
    public MuseumJson toMuseumJson(@Nonnull MuseumResponse response) {
        return new MuseumJson(
                UUID.fromString(response.getId()),
                response.getTitle(),
                response.getDescription(),
                response.getPhoto(),
                new GeoJson(
                        response.getGeo().getCity(),
                        new CountryJson(
                                UUID.fromString(response.getGeo().getCountry().getId()),
                                response.getGeo().getCountry().getName()
                        )
                )
        );
    }
}