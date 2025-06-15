package anbrain.qa.rococo.service;

import anbrain.qa.rococo.grpc.MuseumRequest;
import anbrain.qa.rococo.grpc.MuseumResponse;
import anbrain.qa.rococo.grpc.MuseumServiceGrpc;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import static anbrain.qa.rococo.exception.GrpcExceptionHandler.handleGrpcException;

@Component
public class MuseumGrpcClient {

    @GrpcClient("rococo-museum")
    private MuseumServiceGrpc.MuseumServiceBlockingStub museumStub;

    public MuseumResponse getMuseum(@Nonnull String id) {
        try {
            return museumStub.getMuseum(MuseumRequest.newBuilder()
                    .setId(id)
                    .build());
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "Museum", id);
        }
    }
}
