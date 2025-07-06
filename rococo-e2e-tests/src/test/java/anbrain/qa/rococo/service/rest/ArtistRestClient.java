package anbrain.qa.rococo.service.rest;

import anbrain.qa.rococo.model.rest.ArtistJson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

import static anbrain.qa.rococo.specification.RestAssuredSpec.*;
import static io.restassured.RestAssured.given;

public class ArtistRestClient {

    private static final String API_ARTIST_PATH = "/api/artist";
    private static final String API_ARTIST_BY_ID_PATH = API_ARTIST_PATH + "/{id}";

    @Step("Получить список художников с пагинацией")
    public Response getAllArtists(int page, int size) {
        return given(gatewayRequestSpec)
                .queryParams(Map.of(
                        "page", page,
                        "size", size
                ))
                .get(API_ARTIST_PATH)
                .then()
                .spec(responseSpec)
                .extract().response();
    }

    @Step("Получить художника по ID {id}")
    public Response getArtistById(@NonNull UUID id) {
        return given(gatewayRequestSpec)
                .pathParam("id", id)
                .get(API_ARTIST_BY_ID_PATH)
                .then()
                .spec(responseSpec)
                .extract().response();
    }

    @Step("Поиск художников по имени {name}")
    public Response searchArtistsByName(@NonNull String name, @NonNull Pageable pageable) {
        return given(gatewayRequestSpec)
                .queryParams(Map.of(
                        "name", name,
                        "page", pageable.getPageNumber(),
                        "size", pageable.getPageSize()
                ))
                .get(API_ARTIST_PATH)
                .then()
                .spec(responseSpec)
                .extract().response();
    }

    @Step("Создать нового художника {artist.name}")
    public Response createArtist(@NonNull ArtistJson artist, @NonNull String token) {
        return given(gatewayRequestSpec)
                .header("Authorization", token)
                .body(artist)
                .post(API_ARTIST_PATH)
                .then()
                .spec(responseSpec)
                .extract().response();
    }

    @Step("Обновить данные художника {artist.name}")
    public Response updateArtist(@NonNull ArtistJson artist, @NonNull String token) {
        return given(gatewayRequestSpec)
                .header("Authorization", token)
                .body(artist)
                .put(API_ARTIST_PATH)
                .then()
                .spec(responseSpec)
                .extract().response();
    }

}