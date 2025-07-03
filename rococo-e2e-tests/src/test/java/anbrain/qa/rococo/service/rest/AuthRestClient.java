package anbrain.qa.rococo.service.rest;

import anbrain.qa.rococo.api.core.ThreadSafeCookieStore;
import anbrain.qa.rococo.config.Config;
import anbrain.qa.rococo.jupiter.extension.ApiLoginExtension;
import anbrain.qa.rococo.utils.OAuthUtils;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Map;

public class AuthRestClient {

    private static final Config CFG = Config.getInstance();
    private final RequestSpecification requestSpec;

    public AuthRestClient() {
        this.requestSpec = RestAssured.given()
                .baseUri(CFG.authUrl())
                .contentType(ContentType.URLENC)
                .log().all();
    }

    @Step("Авторизация пользователя по API - username: {username}, password: {password}")
    @SneakyThrows
    public String login(String username, String password) {

        Response loginPage = requestSpec.get("/login");
        String xsrfToken = getXsrfTokenFromResponse(loginPage);

        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        final String client_id = "client";

        requestSpec
                .queryParams(Map.of(
                        "response_type", "code",
                        "client_id", client_id,
                        "scope", "openid",
                        "redirect_uri", redirectUri,
                        "code_challenge", codeChallenge,
                        "code_challenge_method", "S256"
                ))
                .get("/oauth2/authorize");

        requestSpec
                .cookie("XSRF-TOKEN", xsrfToken)
                .formParams(Map.of(
                        "username", username,
                        "password", password,
                        "_csrf", xsrfToken
                ))
                .post("/login");

        Response tokenResponse = requestSpec
                .formParams(Map.of(
                        "client_id", client_id,
                        "redirect_uri", redirectUri,
                        "grant_type", "authorization_code",
                        "code", ApiLoginExtension.getCode(),
                        "code_verifier", codeVerifier
                ))
                .post("/oauth2/token");

        return tokenResponse.jsonPath().getString("id_token");
    }

    @Step("Регистрация пользователя по API - username: {username}, password: {password}")
    public void register(String username, String password, String passwordSubmit) {
        Response registerPage = requestSpec.get("/register");
        String xsrfToken = getXsrfTokenFromResponse(registerPage);

        requestSpec
                .cookie("XSRF-TOKEN", xsrfToken)
                .formParams(Map.of(
                        "username", username,
                        "password", password,
                        "passwordSubmit", passwordSubmit,
                        "_csrf", xsrfToken
                ))
                .post("/register");
    }

    private String getXsrfTokenFromResponse(@NonNull Response response) {
        String xsrfToken = response.getCookie("XSRF-TOKEN");

        if (xsrfToken == null || xsrfToken.isEmpty()) {
            try {
                xsrfToken = ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN");
            } catch (Exception e) {
                throw new IllegalStateException("XSRF-TOKEN не найден в хранилище и в cookie");
            }
        }

        return xsrfToken;
    }
}