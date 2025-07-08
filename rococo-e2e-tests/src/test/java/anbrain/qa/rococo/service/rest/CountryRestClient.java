package anbrain.qa.rococo.service.rest;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static anbrain.qa.rococo.specification.RestAssuredSpec.gatewayRequestSpec;
import static anbrain.qa.rococo.specification.RestAssuredSpec.responseSpec;
import static io.restassured.RestAssured.given;

public class CountryRestClient {

    private static final String API_COUNTRY_PATH = "/api/country";

    @Step("Получение списка стран с пагинацией")
    public Response getAllCountries(int page, int size, String token) {
        return given(gatewayRequestSpec)
                .header("Authorization", token)
                .queryParams(
                        "page", page,
                        "size", size
                )
                .get(API_COUNTRY_PATH)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }
}