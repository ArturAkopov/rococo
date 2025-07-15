package anbrain.qa.rococo.page.alert;

import lombok.Getter;

@Getter
public enum FormError {
    ALLOWED_USERNAME_LENGTH_SHOULD_BE_FROM_3_TO_50_CHARACTERS("Allowed username length should be from 3 to 50 characters"),
    ALLOWED_PASSWORD_LENGTH_SHOULD_BE_FROM_3_TO_12_CHARACTERS("Allowed password length should be from 3 to 12 characters"),
    BAD_CREDENTIALS("Неверные учетные данные пользователя"),
    PASSWORDS_SHOULD_BE_EQUAL("Passwords should be equal");

    private final String message;

    FormError(String message) {
        this.message = message;
    }

}
