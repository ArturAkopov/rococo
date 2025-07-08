package anbrain.qa.rococo.service.rest;

import anbrain.qa.rococo.model.rest.UserJson;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static anbrain.qa.rococo.specification.RestAssuredSpec.gatewayRequestSpec;
import static anbrain.qa.rococo.specification.RestAssuredSpec.responseSpec;
import static io.restassured.RestAssured.given;

public class UserRestClient {

    private static final String API_USER = "/api/user";

    @Step("Получение информации о текущем пользователе")
    public Response getCurrentUser(String token) {
        return given(gatewayRequestSpec)
                .header("Authorization", token)
                .get(API_USER)
                .then().spec(responseSpec)
                .extract().response();
    }

    @Step("Обновить данные пользователя '{user.username}'")
    public Response updateUser(UserJson user, String token) {
        return given(gatewayRequestSpec)
                .header("Authorization", token)
                .body(user)
                .patch(API_USER)
                .then().spec(responseSpec)
                .extract().response();
    }
}