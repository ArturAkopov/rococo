package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.config.Config;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.rest.ArtistJson;
import anbrain.qa.rococo.utils.AllureGrpcInterceptor;
import anbrain.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.Step;
import lombok.NonNull;


public class ArtistGrpcClient {

    private final Config CFG = Config.getInstance();

    private final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.artistGrpcAddress(), CFG.artistGrpcPort())
            .intercept(new AllureGrpcInterceptor())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    private final ArtistServiceGrpc.ArtistServiceBlockingStub blockingStub
            = ArtistServiceGrpc.newBlockingStub(channel);

    @Step("Создание нового художника - {artistJson.name} по grpc")
    public ArtistJson createArtist(@NonNull ArtistJson artistJson) {
        final ArtistResponse response = blockingStub.createArtist(CreateArtistRequest.newBuilder()
                .setName(artistJson.name())
                .setBiography(artistJson.biography())
                .setPhoto(artistJson.photo())
                .build());
        return ArtistJson.fromGrpcResponse(response);
    }

    @Step("Поиск художника по имени - {name} по grpc")
    public ArtistJson searchArtistsByName(String name) {
        final AllArtistsResponse response = blockingStub.searchArtistsByName(SearchArtistsRequest.newBuilder()
                .setName(name)
                .setPage(0)
                .setSize(10)
                .build());

        if (response.getArtistsList().isEmpty()) {
            return null;
        }

        return ArtistJson.fromGrpcResponse(response.getArtistsList().getFirst());
    }

}
