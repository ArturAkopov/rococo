package anbrain.qa.rococo.service.rest;

import anbrain.qa.rococo.api.core.ThreadSafeCookieStore;
import anbrain.qa.rococo.config.Config;
import anbrain.qa.rococo.specification.RestAssuredSpec;
import anbrain.qa.rococo.utils.OAuthUtils;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthRestClient {

    private static final Config CFG = Config.getInstance();
    private static final String CLIENT_ID = "client";
    private static final String OAUTH2_AUTHORIZE_PATH = "/oauth2/authorize";
    private static final String OAUTH2_TOKEN_PATH = "/oauth2/token";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    @Step("Авторизация пользователя по API - username: {username}, password: {password}")
    @SneakyThrows
    public String login(String username, String password) {
        String codeVerifier = OAuthUtils.generateCodeVerifier();
        String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        String redirectUri = CFG.frontUrl() + "authorized";

        Response authResponse = executeOAuth2Authorization(codeChallenge, redirectUri);
        Response loginPage = getLoginPage(authResponse);
        Response loginSubmitResponse = submitLoginForm(username, password, loginPage);
        Response finalAuthResponse = executeOAuth2Authorization(codeChallenge, redirectUri, loginSubmitResponse.getCookies());

        String location = finalAuthResponse.getHeader("Location");
        String code = StringUtils.substringAfter(location, "code=");

        return obtainAuthToken(redirectUri, codeVerifier, code);
    }

    @Step("Получение страницы логина")
    private Response getLoginPage(@NonNull Response authResponse) {
        return given(RestAssuredSpec.authRequestSpec)
                .cookies(authResponse.getCookies())
                .get(LOGIN_PATH)
                .then()
                .spec(RestAssuredSpec.response200)
                .extract()
                .response();
    }

    @Step("Выполнение OAuth2 авторизации")
    private Response executeOAuth2Authorization(String codeChallenge, String redirectUri) {
        return executeOAuth2Authorization(codeChallenge, redirectUri, null);
    }

    @Step("Выполнение OAuth2 авторизации с cookies")
    private Response executeOAuth2Authorization(String codeChallenge, String redirectUri, Map<String, String> cookies) {
        return given(RestAssuredSpec.authRequestSpec)
                .cookies(cookies != null ? cookies : Map.of())
                .queryParams(Map.of(
                        "response_type", "code",
                        "client_id", CLIENT_ID,
                        "scope", "openid",
                        "redirect_uri", redirectUri,
                        "code_challenge", codeChallenge,
                        "code_challenge_method", "S256"
                ))
                .redirects().follow(false)
                .get(OAUTH2_AUTHORIZE_PATH)
                .then()
                .spec(RestAssuredSpec.response302)
                .extract()
                .response();
    }

    @Step("Отправка формы логина")
    private Response submitLoginForm(String username, String password, @NonNull Response loginPage) {
        return given(RestAssuredSpec.authRequestSpec)
                .cookies(loginPage.getCookies())
                .formParams(Map.of(
                        "username", username,
                        "password", password,
                        "_csrf", getXsrfToken(loginPage)
                ))
                .redirects().follow(false)
                .post(LOGIN_PATH)
                .then()
                .spec(RestAssuredSpec.response302)
                .extract()
                .response();
    }

    @Step("Получение токена авторизации")
    private String obtainAuthToken(String redirectUri, String codeVerifier, String code) {
        Response tokenResponse = given(RestAssuredSpec.authRequestSpec)
                .formParams(
                        "client_id", CLIENT_ID,
                        "redirect_uri", redirectUri,
                        "grant_type", "authorization_code",
                        "code", code,
                        "code_verifier", codeVerifier
                )
                .post(OAUTH2_TOKEN_PATH)
                .then()
                .spec(RestAssuredSpec.response200)
                .extract()
                .response();

        return tokenResponse.jsonPath().getString("id_token");
    }

    @Step("Регистрация пользователя по API - username: {username}, password: {password}")
    public void register(String username, String password, String passwordSubmit) {
        Response registerPage = getRegisterPage();
        submitRegistrationData(username, password, passwordSubmit, registerPage);
    }

    @Step("Получение страницы регистрации")
    private Response getRegisterPage() {
        return given(RestAssuredSpec.authRequestSpec)
                .get(REGISTER_PATH)
                .then()
                .spec(RestAssuredSpec.response200)
                .extract()
                .response();
    }

    @Step("Отправка формы регистрации")
    private void submitRegistrationData(String username, String password,
                                        String passwordSubmit, @NonNull Response registerPage) {
        given(RestAssuredSpec.authRequestSpec)
                .cookies(registerPage.getCookies())
                .formParams(Map.of(
                        "username", username,
                        "password", password,
                        "passwordSubmit", passwordSubmit,
                        "_csrf", getXsrfToken(registerPage)
                ))
                .post(REGISTER_PATH)
                .then()
                .spec(RestAssuredSpec.response201);
    }

    private String getXsrfToken(@NonNull Response response) {
        String xsrfToken = response.getCookie("XSRF-TOKEN");

        if (StringUtils.isBlank(xsrfToken)) {
            try {
                xsrfToken = ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN");
            } catch (Exception e) {
                throw new IllegalStateException("XSRF-TOKEN не найден в cookies или хранилище");
            }
        }

        return xsrfToken;
    }
}