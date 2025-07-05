package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.config.Config;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.rest.CountryJson;
import anbrain.qa.rococo.model.rest.MuseumJson;
import anbrain.qa.rococo.utils.AllureGrpcInterceptor;
import anbrain.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.Step;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;

import java.util.Random;
import java.util.UUID;


public class MuseumGrpcClient {

    private final Config CFG = Config.getInstance();

    private final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.museumGrpcAddress(), CFG.museumGrpcPort())
            .intercept(new AllureGrpcInterceptor())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    private final MuseumServiceGrpc.MuseumServiceBlockingStub blockingStub
            = MuseumServiceGrpc.newBlockingStub(channel);

    private final CountryServiceGrpc.CountryServiceBlockingStub countryBlockingStub
            = CountryServiceGrpc.newBlockingStub(channel);

    @Step("Поиск музеев по названию - {title} по grpc")
    public MuseumJson searchMuseumsByTitle(String title) {
        final SearchMuseumsResponse response = blockingStub.searchMuseumsByTitle(
                SearchMuseumsRequest.newBuilder()
                        .setTitle(title)
                        .build()
        );
        if (response.getMuseumsList().isEmpty()) {
            return null;
        }
        return MuseumJson.fromGrpcResponse(response.getMuseums(0));
    }

    @Step("Создание нового музея с названием - {museumJson.title} по grpc")
    public MuseumJson createMuseum(@NonNull MuseumJson museumJson) {
        final MuseumResponse response = blockingStub.createMuseum(CreateMuseumRequest.newBuilder()
                .setTitle(museumJson.title())
                .setDescription(museumJson.description())
                .setPhoto(museumJson.photo())
                .setGeo(
                        Geo.newBuilder()
                                .setCity(museumJson.geo().city())
                                .setCountry(
                                        Country.newBuilder()
                                                .setId(String.valueOf(museumJson.geo().country().id()))
                                                .setName(museumJson.geo().country().name())
                                ).build()
                )
                .build());
        return MuseumJson.fromGrpcResponse(response);
    }

    @Step("Получение случайной страны по grpc")
    public CountryJson getRandomCountry(int page, int size) {
        final AllCountriesResponse response = countryBlockingStub.getAllCountries(AllCountriesRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .build());

        if (response.getCountriesList().isEmpty()) {
            return null;
        }

        final int randomIndex = new Random().nextInt(response.getCountriesList().size());
        final Country randomCountry = response.getCountriesList().get(randomIndex);
        return new CountryJson(
                UUID.fromString(randomCountry.getId()),
                randomCountry.getName()
        );
    }

    @Step("Получение страны - {name} по grpc")
    public CountryJson getCountryByName(int page, int size, String name) {
        final AllCountriesResponse response = countryBlockingStub.getAllCountries(AllCountriesRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .setName(name)
                .build());

        if (response.getCountriesList().isEmpty()) {
            return null;
        }

        final Country country = response.getCountriesList().getFirst();
        return new CountryJson(
                UUID.fromString(country.getId()),
                country.getName()
        );
    }
}
