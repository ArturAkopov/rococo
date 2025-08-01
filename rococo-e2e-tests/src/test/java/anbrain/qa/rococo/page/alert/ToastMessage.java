package anbrain.qa.rococo.page.alert;

import lombok.Getter;

@Getter
public enum ToastMessage {
    SESSION_IS_OVER("Сессия завершена"),
    PROFILE_UPDATED("Профиль обновлен"),
    PASSWORDS_SHOULD_BE_EQUAL("Passwords should be equal"),
    UPDATED_PAINTING("Обновлена картина: "),
    ADDED_PAINTINGS("Добавлена картины: "),
    UPDATED_MUSEUM("Обновлен музей: "),
    ADDED_MUSEUM("Добавлен музей: "),
    ADDED_ARTIST("Добавлен художник: "),
    UPDATED_ARTIST("Обновлен художник: ");

    private final String message;

    ToastMessage(String message) {
        this.message = message;
    }

}
