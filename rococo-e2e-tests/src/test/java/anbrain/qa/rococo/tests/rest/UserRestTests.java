package anbrain.qa.rococo.tests.rest;

import anbrain.qa.rococo.jupiter.annotation.ApiLogin;
import anbrain.qa.rococo.jupiter.annotation.Token;
import anbrain.qa.rococo.jupiter.annotation.User;
import anbrain.qa.rococo.jupiter.annotation.meta.RestTest;
import anbrain.qa.rococo.jupiter.extension.ApiLoginExtension;
import anbrain.qa.rococo.model.rest.UserJson;
import anbrain.qa.rococo.service.rest.UserRestClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import io.restassured.response.Response;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("[REST] Проверка работы с сервисом User")
public class UserRestTests {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final UserRestClient userClient = new UserRestClient();

    @Test
    @User
    @ApiLogin
    @DisplayName("Должна успешно возвращаться информация о текущем пользователе")
    void shouldGetCurrentUser(@Token String testToken, @NonNull UserJson expectedUser) {
        UserJson actualUser = userClient.getCurrentUser(testToken)
                .as(UserJson.class);

        assertAll(
                () -> assertNotNull(actualUser.username()),
                () -> assertEquals(expectedUser.username(), actualUser.username()),
                () -> assertNotNull(actualUser.firstname()),
                () -> assertNotNull(actualUser.lastname()),
                () -> assertNotNull(actualUser.avatar())
        );
    }

    @Test
    @DisplayName("Должна вернуться ошибка при запросе без авторизации")
    void shouldFailWhenUnauthorized() {
        Response response = userClient.getCurrentUser("");

        assertEquals(401, response.getStatusCode());
        assertEquals("401 UNAUTHORIZED", response.jsonPath().getString("error.code"));
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Должны успешно обновиться данные пользователя")
    void shouldUpdateUser(@Token String testToken, @NonNull UserJson initialUser) {
        UserJson updateRequest = new UserJson(
                initialUser.id(),
                initialUser.username(),
                RandomDataUtils.randomFirstName(),
                RandomDataUtils.randomLastName(),
                RandomDataUtils.avatar()
        );

        UserJson updatedUser = userClient.updateUser(updateRequest, testToken)
                .as(UserJson.class);

        assertAll(
                () -> assertEquals(updateRequest.username(), updatedUser.username()),
                () -> assertEquals(updateRequest.firstname(), updatedUser.firstname()),
                () -> assertEquals(updateRequest.lastname(), updatedUser.lastname()),
                () -> assertEquals(updateRequest.avatar(), updatedUser.avatar())
        );
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Должна вернуться ошибка при обновлении с пустым именем")
    void shouldFailWhenUpdatingWithEmptyFirstname(@Token String testToken) {
        UserJson invalidUser = new UserJson(
                null,
                "",
                RandomDataUtils.randomFirstName(),
                RandomDataUtils.randomLastName(),
                RandomDataUtils.avatar()
        );

        Response response = userClient.updateUser(invalidUser, testToken);

        assertEquals(400, response.getStatusCode());
        assertEquals("400 BAD_REQUEST", response.jsonPath().getString("error.code"));
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Должна вернуться ошибка при обновлении с пустой фамилией")
    void shouldFailWhenUpdatingWithEmptyLastname(@Token String testToken) {
        UserJson invalidUser = new UserJson(
                null,
                "testuser",
                "John",
                "",
                RandomDataUtils.avatar()
        );

        Response response = userClient.updateUser(invalidUser, testToken);

        assertEquals(400, response.getStatusCode());
        assertEquals("400 BAD_REQUEST", response.jsonPath().getString("error.code"));
    }

    @Test
    @DisplayName("Должна вернуться ошибка при обновлении без авторизации")
    void shouldFailWhenUpdatingUnauthorized() {
        UserJson user = new UserJson(
                null,
                "testuser",
                "John",
                "Doe",
                RandomDataUtils.avatar()
        );

        Response response = userClient.updateUser(user, "");

        assertEquals(401, response.getStatusCode());
        assertEquals("401 UNAUTHORIZED", response.jsonPath().getString("error.code"));
    }
}