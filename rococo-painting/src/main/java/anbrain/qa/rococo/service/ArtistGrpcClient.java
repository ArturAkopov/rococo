package anbrain.qa.rococo.service;

import anbrain.qa.rococo.grpc.ArtistRequest;
import anbrain.qa.rococo.grpc.ArtistResponse;
import anbrain.qa.rococo.grpc.ArtistServiceGrpc;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import static anbrain.qa.rococo.exception.GrpcExceptionHandler.handleGrpcException;
@Service
public class ArtistGrpcClient {

    @GrpcClient("static://localhost:9091?negotiationType=plaintext")
    private ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    public ArtistResponse getArtist(@Nonnull String id) {
        try {
            return artistStub.getArtist(
                    ArtistRequest.newBuilder()
                            .setId(id)
                            .build());
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Artist", id);
        }
    }
}
