package anbrain.qa.rococo.service.rest;

import anbrain.qa.rococo.model.rest.MuseumJson;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static anbrain.qa.rococo.specification.RestAssuredSpec.gatewayRequestSpec;
import static io.restassured.RestAssured.given;

public class MuseumRestClient {

    private static final String API_MUSEUM = "/api/museum";

    @Step("Получить все музеи с пагинацией")
    public Response getAllMuseums(int page, int size) {
        return given(gatewayRequestSpec)
                .queryParam("page", page)
                .queryParam("size", size)
                .get(API_MUSEUM);
    }

    @Step("Получить музей по ID {id}")
    public Response getMuseumById(String id) {
        return given(gatewayRequestSpec)
                .get(API_MUSEUM + "/{id}", id);
    }

    @Step("Поиск музеев по названию '{title}'")
    public Response searchMuseumsByTitle(String title, int page, int size) {
        return given(gatewayRequestSpec)
                .queryParam("title", title)
                .queryParam("page", page)
                .queryParam("size", size)
                .get(API_MUSEUM);
    }

    @Step("Создать новый музей '{museum.title}'")
    public Response createMuseum(MuseumJson museum, String token) {
        return given(gatewayRequestSpec)
                .header("Authorization",token)
                .body(museum)
                .post(API_MUSEUM);
    }

    @Step("Обновить данные музея '{museum.title}'")
    public Response updateMuseum(MuseumJson museum, String token) {
        return given(gatewayRequestSpec)
                .header("Authorization",token)
                .body(museum)
                .put(API_MUSEUM);
    }
}