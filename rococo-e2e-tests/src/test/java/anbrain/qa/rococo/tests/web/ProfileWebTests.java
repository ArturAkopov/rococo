package anbrain.qa.rococo.tests.web;

import anbrain.qa.rococo.jupiter.annotation.ApiLogin;
import anbrain.qa.rococo.jupiter.annotation.User;
import anbrain.qa.rococo.jupiter.annotation.meta.WebTest;
import anbrain.qa.rococo.model.rest.UserJson;
import anbrain.qa.rococo.page.LoginPage;
import anbrain.qa.rococo.page.MainPage;
import anbrain.qa.rococo.page.utils.FormError;
import anbrain.qa.rococo.utils.RandomDataUtils;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@WebTest
@DisplayName("[WEB] Проверка редактирования профиля клиента")
public class ProfileWebTests {

    @Test
    @DisplayName("Пользователь должен быть разавторизован")
    @User
    @ApiLogin
    void shouldLogoutUserSuccess() {
        new MainPage().open()
                .getHeader()
                .toProfileModal()
                .logout()
                .checkThatPageLoaded()
                .checkToastMessage("Сессия завершена")
                .getHeader()
                .checkThatUserUnauthorized();
    }

    @Test
    @User
    @ApiLogin
    @DisplayName("Успешное обновление профиля")
    void shouldUpdateUserSuccess() {
        new MainPage().open()
                .getHeader()
                .toProfileModal()
                .setFirstName((RandomDataUtils.randomFirstName() + "a".repeat(255)).substring(0, 255))
                .setSurname((RandomDataUtils.randomLastName() + "a".repeat(255)).substring(0, 255))
                .uploadAvatar(RandomDataUtils.avatarFile())
                .saveChanges()
                .checkToastMessage("Профиль обновлен");
    }

    @NonNull
    private static Stream<Arguments> provideInvalidUserData() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomFirstName() + "a".repeat(255),
                        RandomDataUtils.randomLastName(),
                        FormError.THE_NAME_CANNOT_BE_LONGER_THAN_255_CHARACTERS.getMessage()),
                Arguments.of(RandomDataUtils.randomFirstName(),
                        RandomDataUtils.randomLastName() + "a".repeat(255),
                        FormError.THE_LAST_NAME_CANNOT_BE_LONGER_THAN_255_CHARACTERS.getMessage())
        );
    }

    @ParameterizedTest
    @User
    @ApiLogin
    @MethodSource("provideInvalidUserData")
    @DisplayName("Невозможность обновить профиль с невалидными данными")
    void shouldLoginUserFailWithInvalidCredentials(String firstName, String surname, String errorText) {
        new MainPage().open()
                .getHeader()
                .toProfileModal()
                .setFirstName(firstName)
                .setSurname(surname)
                .uploadAvatar(RandomDataUtils.avatarFile())
                .invalidSaveChanges()
                .checkErrorMessage(errorText);
    }

}
