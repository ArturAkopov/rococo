package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.config.Config;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.rest.PaintingJson;
import anbrain.qa.rococo.utils.AllureGrpcInterceptor;
import anbrain.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.Step;
import lombok.NonNull;


public class PaintingGrpcClient {

    private final Config CFG = Config.getInstance();

    private final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.paintingGrpcAddress(), CFG.paintingGrpcPort())
            .intercept(new AllureGrpcInterceptor())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    private final PaintingServiceGrpc.PaintingServiceBlockingStub blockingStub
            = PaintingServiceGrpc.newBlockingStub(channel);

    @Step("Создание новой картины - {paintingJson.title} по grpc")
    public PaintingJson createPainting(@NonNull PaintingJson paintingJson) {
        final PaintingResponse response = blockingStub.createPainting(CreatePaintingRequest.newBuilder()
                .setTitle(paintingJson.title())
                .setDescription(paintingJson.description())
                .setContent(paintingJson.content())
                .setMuseumId(paintingJson.museumId())
                .setArtistId(paintingJson.artistId())
                .build());
        return PaintingJson.fromGrpcResponse(response);
    }

    @Step("Поиск картины по названию - {title} по grpc")
    public PaintingJson searchPaintingsByTitle(String title) {
        final AllPaintingsResponse response = blockingStub.getPaintingsByTitle(PaintingsByTitleRequest.newBuilder()
                .setTitle(title)
                .setPage(0)
                .setSize(10)
                .build());

        if (response.getPaintingsList().isEmpty()) {
            return null;
        }

        return PaintingJson.fromGrpcResponse(response.getPaintingsList().getFirst());
    }

}
