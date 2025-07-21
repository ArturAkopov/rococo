package anbrain.qa.rococo.page.alert;

import lombok.Getter;

@Getter
public enum TextError {
    THE_NAME_CANNOT_BE_LONGER_THAN_255_CHARACTERS("Имя не может быть длиннее 255 символов"),
    THE_LAST_NAME_CANNOT_BE_LONGER_THAN_255_CHARACTERS("Фамилия не может быть длиннее 255 символов"),
    THE_TITLE_CANNOT_BE_SHORTER_THAN_3_CHARACTERS("Название не может быть короче 3 символов"),
    THE_TITLE_CANNOT_BE_LONGER_THAN_255_CHARACTERS("Название не может быть длиннее 255 символов"),
    THE_DESCRIPTION_CANNOT_BE_SHORTER_THAN_10_CHARACTERS("Описание не может быть короче 10 символов"),
    THE_NAME_CANNOT_BE_SHORTER_THAN_3_CHARACTERS("Имя не может быть короче 3 символов"),
    THE_BIOGRAPHY_CANNOT_BE_SHORTER_THAN_10_CHARACTERS("Биография не может быть короче 10 символов"),
    THE_BIOGRAPHY_CANNOT_BE_LONGER_THAN_2000_CHARACTERS("Биография не может быть длиннее 2000 символов");

    private final String message;

    TextError(String message) {
        this.message = message;
    }

}
