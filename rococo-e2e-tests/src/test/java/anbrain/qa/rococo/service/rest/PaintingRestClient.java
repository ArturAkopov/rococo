package anbrain.qa.rococo.service.rest;

import anbrain.qa.rococo.model.rest.PaintingFullJson;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static anbrain.qa.rococo.specification.RestAssuredSpec.gatewayRequestSpec;
import static anbrain.qa.rococo.specification.RestAssuredSpec.responseSpec;
import static io.restassured.RestAssured.given;

public class PaintingRestClient {

    private static final String API_PAINTING = "/api/painting";

    @Step("Получение всех картин с пагинацией")
    public Response getAllPaintings(int page, int size) {
        return given(gatewayRequestSpec)
                .queryParam("page", page)
                .queryParam("size", size)
                .get(API_PAINTING)
                .then().spec(responseSpec)
                .extract().response();
    }

    @Step("Получение картины по ID {id}")
    public Response getPaintingById(String id) {
        return given(gatewayRequestSpec)
                .get(API_PAINTING + "/{id}", id)
                .then().spec(responseSpec)
                .extract().response();
    }

    @Step("Получение картин художника {artistId}")
    public Response getPaintingsByArtist(String artistId, int page, int size) {
        return given(gatewayRequestSpec)
                .queryParam("page", page)
                .queryParam("size", size)
                .get(API_PAINTING + "/author/{artistId}", artistId)
                .then().spec(responseSpec)
                .extract().response();
    }

    @Step("Поиск картин по названию '{title}'")
    public Response getPaintingsByTitle(String title, int page, int size) {
        return given(gatewayRequestSpec)
                .queryParam("title", title)
                .queryParam("page", page)
                .queryParam("size", size)
                .get(API_PAINTING)
                .then().spec(responseSpec)
                .extract().response();
    }

    @Step("Создать новую картину '{painting.title}'")
    public Response createPainting(PaintingFullJson painting, String token) {
        return given(gatewayRequestSpec)
                .header("Authorization", token)
                .body(painting)
                .post(API_PAINTING)
                .then().spec(responseSpec)
                .extract().response();
    }

    @Step("Обновить данные картины '{painting.title}'")
    public Response updatePainting(PaintingFullJson painting, String token) {
        return given(gatewayRequestSpec)
                .header("Authorization", token)
                .body(painting)
                .patch(API_PAINTING)
                .then().spec(responseSpec)
                .extract().response();
    }
}