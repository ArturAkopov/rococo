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

    @Autowired
    public PaintingGrpcService(PaintingClient paintingClient) {
        this.paintingClient = paintingClient;
    }

    @Override
    public void getPainting(PaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            PaintingEntity painting = paintingClient.getPainting(id);

            responseObserver.onNext(buildPaintingResponse(painting));
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
                responseBuilder.addPaintings(buildPaintingResponse(painting));
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

            AllPaintingsResponse.Builder responseBuilder = AllPaintingsResponse.newBuilder()
                    .setTotalCount((int) paintings.getTotalElements());

            paintings.getContent().forEach(painting -> {
                responseBuilder.addPaintings(buildPaintingResponse(painting));
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

            responseObserver.onNext(buildPaintingResponse(savedPainting));
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

            responseObserver.onNext(buildPaintingResponse(updatedPainting));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Nonnull
    private PaintingResponse buildPaintingResponse(@Nonnull PaintingEntity painting) {
        return PaintingResponse.newBuilder()
                .setId(painting.getId().toString())
                .setTitle(painting.getTitle())
                .setDescription(painting.getDescription())
                .setContent(new String(painting.getContent()))
                .setMuseumId(String.valueOf(painting.getMuseumId()))
                .setArtistId(String.valueOf(painting.getArtistId())).
                build();
    }

}