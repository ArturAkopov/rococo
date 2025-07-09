package anbrain.qa.rococo.tests.rest;

import anbrain.qa.rococo.jupiter.annotation.meta.RestTest;
import anbrain.qa.rococo.service.rest.AuthRestClient;
import anbrain.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("[REST] Проверки авторизации и регистрации")
public class AuthRestTests {

    private final AuthRestClient authClient = new AuthRestClient();
    private final String defaultPassword = "12345";
    private final String username = RandomDataUtils.randomUsername();

    @Test
    @DisplayName("Регистрация нового пользователя")
    void shouldRegisterNewUser() {
        assertDoesNotThrow(() -> authClient.register(username, defaultPassword, defaultPassword),
                "Регистрация должна завершиться успешно");
    }

    @Test
    @DisplayName("Нельзя зарегистрировать пользователя с существующим именем")
    void shouldNotRegisterDuplicateUsername() {
        authClient.register(username, defaultPassword, defaultPassword);

        assertThrows(AssertionError.class,
                () -> authClient.register(username, defaultPassword, defaultPassword),
                "При регистрации с существующим именем должно быть исключение");
    }

    @Test
    @DisplayName("Нельзя зарегистрироваться с несовпадающими паролями")
    void shouldNotRegisterWithPasswordMismatch() {
        String invalidPassword = "invalid";

        assertThrows(AssertionError.class,
                () -> authClient.register(username, defaultPassword, invalidPassword),
                "При несовпадающих паролях должно быть исключение");
    }

    @Test
    @DisplayName("Нельзя зарегистрироваться с пустым именем пользователя")
    void shouldNotRegisterWithEmptyUsername() {
        String emptyUsername = "";

        assertThrows(AssertionError.class,
                () -> authClient.register(emptyUsername, defaultPassword, defaultPassword),
                "При пустом имени пользователя должно быть исключение");
    }

    @Test
    @DisplayName("Нельзя зарегистрироваться с пустым паролем")
    void shouldNotRegisterWithEmptyPassword() {
        String emptyPassword = "";

        assertThrows(AssertionError.class,
                () -> authClient.register(username, emptyPassword, emptyPassword),
                "При пустом пароле должно быть исключение");
    }

    @Test
    @DisplayName("Успешная авторизация после регистрации")
    void shouldSuccessfullyLoginAfterRegistration() {

        authClient.register(username, defaultPassword, defaultPassword);

        String token = authClient.login(username, defaultPassword);

        assertNotNull(token, "Токен авторизации не должен быть null");
        assertFalse(token.isEmpty(), "Токен авторизации не должен быть пустым");
    }

    @Test
    @DisplayName("Нельзя авторизоваться с неправильным паролем")
    void shouldNotLoginWithWrongPassword() {
        String invalidPassword = "invalid";

        authClient.register(username, defaultPassword, defaultPassword);

        assertThrows(AssertionError.class, () -> authClient.login(username, invalidPassword),
                "При авторизации с неправильным паролем должно быть исключение");
    }

    @Test
    @DisplayName("Нельзя авторизоваться с несуществующим пользователем")
    void shouldNotLoginWithNonExistentUser() {
        String username = RandomDataUtils.randomUsername();
        String password = RandomDataUtils.randomUsername();

        assertThrows(AssertionError.class, () -> authClient.login(username, password),
                "При авторизации несуществующего пользователя должно быть исключение");
    }

}