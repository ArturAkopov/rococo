package anbrain.qa.rococo.tests.web;

import anbrain.qa.rococo.jupiter.annotation.User;
import anbrain.qa.rococo.jupiter.annotation.meta.WebTest;
import anbrain.qa.rococo.model.rest.UserJson;
import anbrain.qa.rococo.page.LoginPage;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@WebTest
@DisplayName("[WEB] Проверка авторизации")
public class LoginWebTests {

    private final String password = "12345";

    @Test
    @DisplayName("Должен быть успешно авторизован пользователь")
    @User
    void shouldLoginUserSuccess(@NonNull UserJson userJson) {
        new LoginPage().open()
                .setUsername(userJson.username())
                .setPassword(password)
                .submitLogin()
                .checkThatPageLoaded()
                .getHeader()
                .checkThatUserAuthorized();
    }

    @ParameterizedTest
    @CsvSource(
            {
                    "'', ''",
                    "'username', ''",
                    "'', 'password'"
            }
            )
    @DisplayName("Невозможность авторизации с невалидными данными")
    void shouldLoginUserFailWithInvalidCredentials(String username, String password) {
        new LoginPage().open()
                .setUsername(username)
                .setPassword(password)
                .submitLogin()
                .checkThatPageUnloaded();
    }

    @Test
    @DisplayName("Невозможность авторизации с невалидным паролем")
    @User
    void shouldLoginUserFailWithInvalidPassword(@NonNull UserJson userJson) {
        new LoginPage().open()
                .setUsername(userJson.username())
                .setPassword("password")
                .submitLogin()
                .checkErrorMessage("Неверные учетные данные пользователя");
    }

}
