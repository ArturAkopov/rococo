package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.*;
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

import static anbrain.qa.rococo.exception.GrpcExceptionHandler.handleGrpcException;

@Service
public class PaintingGrpcClient {

    @GrpcClient("rococo-painting")
    private PaintingServiceGrpc.PaintingServiceBlockingStub paintingStub;

    public PaintingJson getPainting(@Nonnull UUID id) {
        try {
            PaintingResponse response = paintingStub.getPainting(
                    PaintingRequest.newBuilder()
                            .setId(id.toString())
                            .build());
            return toPaintingJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Painting", id.toString());
        }
    }

    public RestPage<PaintingJson> getAllPaintings(@Nonnull Pageable pageable) {
        try {
            AllPaintingsResponse response = paintingStub.getAllPaintings(
                    AllPaintingsRequest.newBuilder()
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<PaintingJson> paintings = response.getPaintingsList().stream()
                    .map(this::toPaintingJson)
                    .collect(Collectors.toList());

            return new RestPage<>(
                    paintings,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Painting", "getAllPaintings");
        }
    }

    public RestPage<PaintingJson> getPaintingsByArtist(@Nonnull UUID artistId, @Nonnull Pageable pageable) {
        try {
            AllPaintingsResponse response = paintingStub.getPaintingsByArtist(
                    PaintingsByArtistRequest.newBuilder()
                            .setArtistId(artistId.toString())
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<PaintingJson> paintings = response.getPaintingsList().stream()
                    .map(this::toPaintingJson)
                    .collect(Collectors.toList());

            return new RestPage<>(
                    paintings,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Painting", "getPaintingsByArtist");
        }
    }

    public PaintingJson createPainting(@Nonnull PaintingJson painting) {
        try {
            PaintingResponse response = paintingStub.createPainting(
                    CreatePaintingRequest.newBuilder()
                            .setTitle(painting.title())
                            .setDescription(painting.description())
                            .setContent(painting.content())
                            .setMuseumId(painting.museum().id().toString())
                            .setArtistId(painting.artist().id().toString())
                            .build());
            return toPaintingJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Painting", "creation request");
        }
    }

    public PaintingJson updatePainting(@Nonnull PaintingJson painting) {
        try {
            PaintingResponse response = paintingStub.updatePainting(
                    UpdatePaintingRequest.newBuilder()
                            .setId(painting.id().toString())
                            .setTitle(painting.title())
                            .setDescription(painting.description())
                            .setContent(painting.content())
                            .setMuseumId(painting.museum().id().toString())
                            .setArtistId(painting.artist().id().toString())
                            .build());
            return toPaintingJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Painting", painting.id().toString());
        }
    }

    @Nonnull
    private PaintingJson toPaintingJson(@Nonnull PaintingResponse response) {
        GeoJson geoJson = null;
        if (response.getMuseum().hasGeo()) {
            Geo geo = response.getMuseum().getGeo();
            geoJson = new GeoJson(
                    geo.getCity(),
                    new CountryJson(
                            UUID.fromString(geo.getCountry().getId()),
                            geo.getCountry().getName()
                    )
            );
        }

        return new PaintingJson(
                UUID.fromString(response.getId()),
                response.getTitle(),
                response.getDescription(),
                response.getContent(),
                new MuseumJson(
                        UUID.fromString(response.getMuseum().getId()),
                        response.getMuseum().getTitle(),
                        response.getMuseum().getDescription(),
                        response.getMuseum().getPhoto(),
                        geoJson
                ),
                new ArtistJson(
                        UUID.fromString(response.getArtist().getId()),
                        response.getArtist().getName(),
                        response.getArtist().getBiography(),
                        response.getArtist().getPhoto()
                )
        );
    }
}