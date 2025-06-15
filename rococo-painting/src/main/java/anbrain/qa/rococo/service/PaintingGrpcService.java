package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.PaintingEntity;
import anbrain.qa.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

@GrpcService
public class PaintingGrpcService extends PaintingServiceGrpc.PaintingServiceImplBase {

    private final PaintingClient paintingClient;
    private final ArtistGrpcClient artistClient;
    private final MuseumGrpcClient museumClient;

    @Autowired
    public PaintingGrpcService(PaintingClient paintingClient,
                               ArtistGrpcClient artistClient,
                               MuseumGrpcClient museumClient) {
        this.paintingClient = paintingClient;
        this.artistClient = artistClient;
        this.museumClient = museumClient;
    }

    @Override
    public void getPainting(PaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            PaintingEntity painting = paintingClient.getPainting(id);

            ArtistResponse artist = artistClient.getArtist(painting.getArtistId().toString());
            MuseumResponse museum = museumClient.getMuseum(painting.getMuseumId().toString());

            responseObserver.onNext(buildPaintingResponse(painting, artist, museum));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAllPaintings(AllPaintingsRequest request, StreamObserver<AllPaintingsResponse> responseObserver) {
        try {
            Page<PaintingEntity> paintings = paintingClient.getAllPaintings(
                    PageRequest.of(request.getPage(), request.getSize()));

            AllPaintingsResponse.Builder responseBuilder = AllPaintingsResponse.newBuilder()
                    .setTotalCount((int) paintings.getTotalElements());

            paintings.getContent().forEach(painting -> {
                ArtistResponse artist = artistClient.getArtist(painting.getArtistId().toString());
                MuseumResponse museum = museumClient.getMuseum(painting.getMuseumId().toString());
                responseBuilder.addPaintings(buildPaintingResponse(painting, artist, museum));
            });

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getPaintingsByArtist(PaintingsByArtistRequest request, StreamObserver<AllPaintingsResponse> responseObserver) {
        try {
            UUID artistId = UUID.fromString(request.getArtistId());
            Page<PaintingEntity> paintings = paintingClient.getPaintingsByArtist(
                    artistId,
                    PageRequest.of(request.getPage(), request.getSize()));

            ArtistResponse artist = artistClient.getArtist(artistId.toString());

            AllPaintingsResponse.Builder responseBuilder = AllPaintingsResponse.newBuilder()
                    .setTotalCount((int) paintings.getTotalElements());

            paintings.getContent().forEach(painting -> {
                MuseumResponse museum = museumClient.getMuseum(painting.getMuseumId().toString());
                responseBuilder.addPaintings(buildPaintingResponseWithArtist(painting, museum, artist));
            });

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void createPainting(CreatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            PaintingEntity painting = new PaintingEntity();
            painting.setTitle(request.getTitle());
            painting.setDescription(request.getDescription());
            painting.setContent(request.getContent().getBytes());
            painting.setArtistId(UUID.fromString(request.getArtistId()));
            painting.setMuseumId(UUID.fromString(request.getMuseumId()));

            PaintingEntity savedPainting = paintingClient.createPainting(painting);

            ArtistResponse artist = artistClient.getArtist(request.getArtistId());
            MuseumResponse museum = museumClient.getMuseum(request.getMuseumId());

            responseObserver.onNext(buildPaintingResponse(savedPainting, artist, museum));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updatePainting(UpdatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            PaintingEntity painting = paintingClient.getPainting(id);

            painting.setTitle(request.getTitle());
            painting.setDescription(request.getDescription());
            painting.setContent(request.getContent().getBytes());
            painting.setArtistId(UUID.fromString(request.getArtistId()));
            painting.setMuseumId(UUID.fromString(request.getMuseumId()));

            PaintingEntity updatedPainting = paintingClient.updatePainting(painting);

            ArtistResponse artist = artistClient.getArtist(request.getArtistId());
            MuseumResponse museum = museumClient.getMuseum(request.getMuseumId());

            responseObserver.onNext(buildPaintingResponse(updatedPainting, artist, museum));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Nonnull
    private PaintingResponse buildPaintingResponse(@Nonnull PaintingEntity painting,
                                                   @Nonnull ArtistResponse artist,
                                                   @Nonnull MuseumResponse museum) {
        return PaintingResponse.newBuilder()
                .setId(painting.getId().toString())
                .setTitle(painting.getTitle())
                .setDescription(painting.getDescription())
                .setContent(new String(painting.getContent()))
                .setArtist(Artist.newBuilder()
                        .setId(artist.getId())
                        .setName(artist.getName())
                        .setBiography(artist.getBiography())
                        .setPhoto(artist.getPhoto())
                        .build())
                .setMuseum(Museum.newBuilder()
                        .setId(museum.getId())
                        .setTitle(museum.getTitle())
                        .setDescription(museum.getDescription())
                        .setPhoto(museum.getPhoto())
                        .setGeo(Geo.newBuilder()
                                .setCity(museum.getGeo().getCity())
                                .setCountry(Country.newBuilder()
                                        .setId(museum.getGeo().getCountry().getId())
                                        .setName(museum.getGeo().getCountry().getName())
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Nonnull
    private PaintingResponse buildPaintingResponseWithArtist(@Nonnull PaintingEntity painting,
                                                             @Nonnull MuseumResponse museum,
                                                             @Nonnull ArtistResponse artist) {
        return PaintingResponse.newBuilder()
                .setId(painting.getId().toString())
                .setTitle(painting.getTitle())
                .setDescription(painting.getDescription())
                .setContent(new String(painting.getContent()))
                .setArtist(Artist.newBuilder()
                        .setId(artist.getId())
                        .setName(artist.getName())
                        .setBiography(artist.getBiography())
                        .setPhoto(artist.getPhoto())
                        .build())
                .setMuseum(Museum.newBuilder()
                        .setId(museum.getId())
                        .setTitle(museum.getTitle())
                        .setDescription(museum.getDescription())
                        .setPhoto(museum.getPhoto())
                        .setGeo(Geo.newBuilder()
                                .setCity(museum.getGeo().getCity())
                                .setCountry(Country.newBuilder()
                                        .setId(museum.getGeo().getCountry().getId())
                                        .setName(museum.getGeo().getCountry().getName())
                                        .build())
                                .build())
                        .build())
                .build();
    }
}