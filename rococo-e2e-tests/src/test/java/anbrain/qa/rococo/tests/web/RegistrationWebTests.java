package anbrain.qa.rococo.tests.web;

import anbrain.qa.rococo.jupiter.annotation.meta.WebTest;
import anbrain.qa.rococo.page.RegisterPage;
import anbrain.qa.rococo.page.utils.FormError;
import anbrain.qa.rococo.utils.RandomDataUtils;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@WebTest
@DisplayName("[WEB] Проверка регистрации Web")
public class RegistrationWebTests {

    @NonNull
    private static Stream<Arguments> provideCredentials() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomString().substring(0, 3), RandomDataUtils.randomString().substring(0, 3)),
                Arguments.of(RandomDataUtils.randomString().substring(0, 50), RandomDataUtils.randomString().substring(0, 12))
        );
    }

    @ParameterizedTest
    @MethodSource("provideCredentials")
    @DisplayName("Должен быть успешно зарегистрирован пользователь")
    void shouldRegisterUserSuccess(String username, String password) {
        new RegisterPage().open()
                .checkThatPageLoaded()
                .setUsername(username)
                .setPassword(password)
                .setSubmitPassword(password)
                .submitRegister()
                .checkThatRegisterSuccess();
    }

    @ParameterizedTest
    @CsvSource(
            {
                    "'username', '', ''",
                    "'', 'password', 'password'",
                    "'', '', ''",
                    "'username', '', 'password'",
                    "'username', 'password', ''"
            }
    )
    @DisplayName("Невозможность регистрации с невалидными данными")
    void shouldRegisterUserFailWithInvalidCredentials(String username, String password, String subPassword) {
        new RegisterPage().open()
                .checkThatPageLoaded()
                .setUsername(username)
                .setPassword(password)
                .setSubmitPassword(subPassword)
                .checkThatRegisterFail();
    }

    @NonNull
    private static Stream<Arguments> provideInvalidCredentials() {
        final String shortUsername = RandomDataUtils.randomString().substring(0, 2);
        final String longUsername = (RandomDataUtils.randomString()+"a".repeat(10)).substring(0, 51);
        final String shortPassword = RandomDataUtils.randomString().substring(0, 2);
        final String longPassword = (RandomDataUtils.randomString()+"a".repeat(10)).substring(0, 51);
        return Stream.of(
                Arguments.of(shortUsername, "12345", "12345",
                        FormError.ALLOWED_USERNAME_LENGTH_SHOULD_BE_FROM_3_TO_50_CHARACTERS.getMessage()),
                Arguments.of("Username", shortPassword, shortPassword,
                        FormError.ALLOWED_PASSWORD_LENGTH_SHOULD_BE_FROM_3_TO_12_CHARACTERS.getMessage()),
                Arguments.of("Username", "12345", "1234",
                        FormError.PASSWORDS_SHOULD_BE_EQUAL.getMessage()),
                Arguments.of(longUsername, "12345", "12345",
                        FormError.ALLOWED_USERNAME_LENGTH_SHOULD_BE_FROM_3_TO_50_CHARACTERS.getMessage()),
                Arguments.of("Username", longPassword, longPassword,
                        FormError.ALLOWED_PASSWORD_LENGTH_SHOULD_BE_FROM_3_TO_12_CHARACTERS.getMessage())
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCredentials")
    @DisplayName("Невозможность регистрации с 3>username>50 или 3>password>12")
    void shouldRegisterUserFailWithVeryShortOrLongCredentials(String username, String password, String submitPassword, String errorText) {
        new RegisterPage().open()
                .checkThatPageLoaded()
                .setUsername(username)
                .setPassword(password)
                .setSubmitPassword(submitPassword)
                .submitRegister()
                .checkErrorMessage(errorText);
    }

}
