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
        Response loginPage = getLoginPage();
        String xsrfToken = getXsrfTokenFromResponse(loginPage);

        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        final String client_id = "client";

        executeOAuth2Authorization(codeChallenge, redirectUri, client_id);
        submitLoginCredentials(username, password, xsrfToken);

        return obtainAuthToken(client_id, redirectUri, codeVerifier);
    }

    @Step("Получение страницы логина")
    private Response getLoginPage() {
        return requestSpec.get("/login");
    }

    @Step("Выполнение OAuth2 авторизации")
    private void executeOAuth2Authorization(String codeChallenge,
                                            String redirectUri, String clientId) {
        requestSpec
                .queryParams(Map.of(
                        "response_type", "code",
                        "client_id", clientId,
                        "scope", "openid",
                        "redirect_uri", redirectUri,
                        "code_challenge", codeChallenge,
                        "code_challenge_method", "S256"
                ))
                .get("/oauth2/authorize");
    }

    @Step("Отправка учетных данных для входа")
    private void submitLoginCredentials(String username, String password, String xsrfToken) {
        requestSpec
                .cookie("XSRF-TOKEN", xsrfToken)
                .formParams(Map.of(
                        "username", username,
                        "password", password,
                        "_csrf", xsrfToken
                ))
                .post("/login");
    }

    @Step("Получение токена авторизации")
    private String obtainAuthToken(String clientId, String redirectUri, String codeVerifier) {
        Response tokenResponse = requestSpec
                .formParams(Map.of(
                        "client_id", clientId,
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
        Response registerPage = getRegisterPage();
        String xsrfToken = getXsrfTokenFromResponse(registerPage);
        submitRegistrationData(username, password, passwordSubmit, xsrfToken);
    }

    @Step("Получение страницы регистрации")
    private Response getRegisterPage() {
        return requestSpec.get("/register");
    }

    @Step("Отправка формы регистрации")
    private void submitRegistrationData(String username, String password,
                                        String passwordSubmit, String xsrfToken) {
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