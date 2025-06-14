package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.GeoJson;
import anbrain.qa.rococo.model.MuseumJson;
import anbrain.qa.rococo.model.page.RestPage;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static anbrain.qa.rococo.utils.GrpcExceptionHandler.handleGrpcException;

@Service
public class MuseumGrpcClient {

    @GrpcClient("rococo-museum")
    private MuseumServiceGrpc.MuseumServiceBlockingStub museumStub;

    public MuseumJson getMuseum(@Nonnull UUID id) {
        try {
            MuseumResponse response = museumStub.getMuseum(
                    MuseumRequest.newBuilder()
                            .setId(id.toString())
                            .build());
            return toMuseumJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Museum", id.toString());
        }
    }

    public RestPage<MuseumJson> getAllMuseums(@Nonnull Pageable pageable) {
        try {
            AllMuseumsResponse response = museumStub.getAllMuseums(
                    AllMuseumsRequest.newBuilder()
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<MuseumJson> museums = response.getMuseumsList().stream()
                    .map(this::toMuseumJson)
                    .collect(Collectors.toList());

            return new RestPage<>(
                    museums,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Museum", "getAllMuseums");
        }
    }

    public List<MuseumJson> searchMuseumsByTitle(String title) {
        try {
            SearchMuseumsResponse response = museumStub.searchMuseumsByTitle(
                    SearchMuseumsRequest.newBuilder()
                            .setTitle(title)
                            .build());

            return response.getMuseumsList().stream()
                    .map(this::toMuseumJson)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Museum", "search by title: " + title);
        }
    }

    public MuseumJson createMuseum(@Nonnull MuseumJson museum) {
        try {
            MuseumResponse response = museumStub.createMuseum(
                    CreateMuseumRequest.newBuilder()
                            .setTitle(museum.title())
                            .setDescription(museum.description())
                            .setPhoto(museum.photo())
                            .setGeo(toGrpcGeo(museum.geo()))
                            .build());
            return toMuseumJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Museum", "creation request");
        }
    }

    public MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
        try {
            MuseumResponse response = museumStub.updateMuseum(
                    UpdateMuseumRequest.newBuilder()
                            .setId(museum.id().toString())
                            .setTitle(museum.title())
                            .setDescription(museum.description())
                            .setPhoto(museum.photo())
                            .setGeo(toGrpcGeo(museum.geo()))
                            .build());
            return toMuseumJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Museum", museum.id().toString());
        }
    }

    @Nonnull
    private Geo toGrpcGeo(@Nonnull GeoJson geo) {
        return Geo.newBuilder()
                .setCity(geo.city())
                .setCountry(Country.newBuilder()
                        .setId(String.valueOf(geo.country().id()))
                        .setName(geo.country().name())
                        .build())
                .build();
    }

    @Nonnull
    private MuseumJson toMuseumJson(@Nonnull MuseumResponse response) {
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
