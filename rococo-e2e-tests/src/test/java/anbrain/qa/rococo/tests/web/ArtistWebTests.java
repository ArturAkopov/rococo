package anbrain.qa.rococo.tests.web;

import anbrain.qa.rococo.jupiter.annotation.*;
import anbrain.qa.rococo.jupiter.annotation.meta.WebTest;
import anbrain.qa.rococo.model.rest.ArtistJson;
import anbrain.qa.rococo.page.ArtistsPage;
import anbrain.qa.rococo.page.alert.TextError;
import anbrain.qa.rococo.page.alert.ToastMessage;
import anbrain.qa.rococo.utils.RandomDataUtils;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@WebTest
@DisplayName("[WEB] Проверка взаимодействия с художниками")
public class ArtistWebTests {

    @NonNull
    private static Stream<Arguments> provideData() {
        return Stream.of(
                Arguments.of(
                        RandomDataUtils.randomPaintingTitle().substring(0, 3),
                        RandomDataUtils.randomString().substring(0, 11)),
                Arguments.of(
                        (RandomDataUtils.randomPaintingTitle() + "a".repeat(255)).substring(0, 255),
                        (RandomDataUtils.randomString() + "a".repeat(2000)).substring(0, 2000))
        );
    }

    @ParameterizedTest
    @User
    @ApiLogin
    @MethodSource("provideData")
    @DisplayName("Должен успешно добавиться художник")
    void shouldAddedArtistSuccess(String name, String biography) {
        new ArtistsPage().open()
                .checkThatPageLoaded()
                .clickAddArtistButton()
                .getEditArtistModal()
                .setArtistName(name)
                .uploadPhoto(RandomDataUtils.avatarFile())
                .setBiography(biography)
                .saveArtist()
                .checkToastMessage(ToastMessage.ADDED_ARTIST.getMessage() + name);
    }

    @Test
    @Artist
    @DisplayName("Должен успешно найтись художник")
    void shouldSearchedArtistSuccess(@NonNull ArtistJson artist) {
        new ArtistsPage().open()
                .checkThatPageLoaded()
                .searchArtistByName(artist.name())
                .checkThatArtistExist(artist.name())
        ;
    }

    @NonNull
    private static Stream<Arguments> provideInvalidData() {
        return Stream.of(
                Arguments.of(
                        RandomDataUtils.randomArtistName().substring(0, 2),
                        RandomDataUtils.randomString(),
                        TextError.THE_NAME_CANNOT_BE_SHORTER_THAN_3_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        (RandomDataUtils.randomArtistName() + "a".repeat(256).substring(0, 256)),
                        RandomDataUtils.randomString(),
                        TextError.THE_NAME_CANNOT_BE_LONGER_THAN_255_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        RandomDataUtils.randomArtistName(),
                        RandomDataUtils.randomString().substring(0, 10),
                        TextError.THE_BIOGRAPHY_CANNOT_BE_SHORTER_THAN_10_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        RandomDataUtils.randomArtistName(),
                        (RandomDataUtils.randomString() + "a".repeat(2000)).substring(0, 2001),
                        TextError.THE_BIOGRAPHY_CANNOT_BE_LONGER_THAN_2000_CHARACTERS.getMessage()
                ));
    }

    @ParameterizedTest
    @User
    @ApiLogin
    @MethodSource("provideInvalidData")
    @DisplayName("Невозможность добавить художника с невалидными данными")
    void shouldAddArtistFailWithInvalidData(String name, String biography, String errorText) {
        new ArtistsPage().open()
                .checkThatPageLoaded()
                .clickAddArtistButton()
                .getEditArtistModal()
                .setArtistName(name)
                .uploadPhoto(RandomDataUtils.avatarFile())
                .setBiography(biography)
                .clickSubmitButton()
                .checkErrorMessage(errorText)
        ;
    }

    @ParameterizedTest
    @User
    @Artist
    @ApiLogin
    @MethodSource("provideData")
    @DisplayName("Должен успешно обновиться художник")
    void shouldUpdatedArtistSuccess(String name, String biography, @NonNull ArtistJson artist) {
        new ArtistsPage().open()
                .checkThatPageLoaded()
                .selectArtist(artist.name())
                .checkThatPageLoaded()
                .clickEditArtistButton()
                .getEditArtistModal()
                .uploadPhoto(RandomDataUtils.avatarFile())
                .setArtistName(name)
                .setBiography(biography)
                .saveArtist()
                .checkToastMessage(ToastMessage.UPDATED_ARTIST.getMessage() + name);
    }

    @ParameterizedTest
    @User
    @Artist
    @ApiLogin
    @MethodSource("provideInvalidData")
    @DisplayName("Невозможность обновить художника с невалидными данными")
    void shouldUpdateArtistFailWithInvalidData(String name, String biography, String errorText, @NonNull ArtistJson artist) {
        new ArtistsPage().open()
                .checkThatPageLoaded()
                .selectArtist(artist.name())
                .checkThatPageLoaded()
                .clickEditArtistButton()
                .getEditArtistModal()
                .uploadPhoto(RandomDataUtils.avatarFile())
                .setArtistName(name)
                .setBiography(biography)
                .clickSubmitButton()
                .checkErrorMessage(errorText);
    }

}
