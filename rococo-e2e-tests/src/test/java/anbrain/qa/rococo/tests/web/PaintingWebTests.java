package anbrain.qa.rococo.tests.web;

import anbrain.qa.rococo.jupiter.annotation.*;
import anbrain.qa.rococo.jupiter.annotation.meta.WebTest;
import anbrain.qa.rococo.model.rest.ArtistJson;
import anbrain.qa.rococo.model.rest.MuseumJson;
import anbrain.qa.rococo.model.rest.PaintingJson;
import anbrain.qa.rococo.page.PaintingsPage;
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
@DisplayName("[WEB] Проверка взаимодействия с картинами")
public class PaintingWebTests {

    private final String description = RandomDataUtils.randomString().substring(0, 11);

    @NonNull
    private static Stream<Arguments> provideData() {
        return Stream.of(
                Arguments.of(RandomDataUtils.randomPaintingTitle().substring(0, 3)),
                Arguments.of((RandomDataUtils.randomPaintingTitle() + "a".repeat(255)).substring(0, 255))
        );
    }

    @ParameterizedTest
    @User
    @Artist
    @Museum
    @ApiLogin
    @MethodSource("provideData")
    @DisplayName("Должна успешно добавиться картина")
    void shouldAddedPaintingSuccess(String title, @NonNull ArtistJson artist, @NonNull MuseumJson museum) {
        new PaintingsPage().open()
                .checkThatPageLoaded()
                .clickAddPaintingButton()
                .getEditPaintingModal()
                .setTitle(title)
                .uploadImage(RandomDataUtils.avatarFile())
                .selectArtist(artist.name())
                .setDescription(description)
                .selectMuseum(museum.title())
                .saveAddPainting()
                .checkToastMessage(ToastMessage.ADDED_PAINTINGS.getMessage() + title);
    }

    @Test
    @User
    @Artist
    @Museum
    @Painting
    @ApiLogin
    @DisplayName("Должна успешно найтись картина")
    void shouldSearchedPaintingSuccess(@NonNull PaintingJson painting) {
        new PaintingsPage().open()
                .checkThatPageLoaded()
                .searchPaintingByTitle(painting.title())
                .checkThatPaintingExist(painting.title())
        ;
    }

    @NonNull
    private static Stream<Arguments> provideInvalidData() {
        return Stream.of(
                Arguments.of(
                        RandomDataUtils.randomPaintingTitle().substring(0, 2),
                        RandomDataUtils.randomString(),
                        TextError.THE_TITLE_CANNOT_BE_SHORTER_THAN_3_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        (RandomDataUtils.randomPaintingTitle() + "a".repeat(256).substring(0, 256)),
                        RandomDataUtils.randomString(),
                        TextError.THE_TITLE_CANNOT_BE_LONGER_THAN_255_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        RandomDataUtils.randomPaintingTitle(),
                        RandomDataUtils.randomString().substring(0, 10),
                        TextError.THE_DESCRIPTION_CANNOT_BE_SHORTER_THAN_10_CHARACTERS.getMessage()
                ));
    }

    @ParameterizedTest
    @User
    @Artist
    @Museum
    @ApiLogin
    @MethodSource("provideInvalidData")
    @DisplayName("Невозможность добавить картину с невалидными данными")
    void shouldAddPaintingFailWithInvalidData(String paintingTitle, String paintingDescription, String errorText,
                                              @NonNull ArtistJson artist, @NonNull MuseumJson museum) {
        new PaintingsPage().open()
                .checkThatPageLoaded()
                .clickAddPaintingButton()
                .getEditPaintingModal()
                .setTitle(paintingTitle)
                .uploadImage(RandomDataUtils.avatarFile())
                .selectArtist(artist.name())
                .setDescription(paintingDescription)
                .selectMuseum(museum.title())
                .clickSubmit()
                .checkErrorMessage(errorText)
        ;
    }

    @ParameterizedTest
    @User
    @Artist
    @Museum
    @Painting
    @ApiLogin
    @MethodSource("provideData")
    @DisplayName("Должна успешно обновиться картина")
    void shouldUpdatedPaintingSuccess(String title, @NonNull PaintingJson painting, @NonNull ArtistJson artist,
                                      @NonNull MuseumJson museum) {
        new PaintingsPage().open()
                .checkThatPageLoaded()
                .selectPainting(painting.title())
                .toEditPainting()
                .uploadImage(RandomDataUtils.avatarFile())
                .setTitle(title)
                .selectArtist(artist.name())
                .setDescription(description)
                .selectMuseum(museum.title())
                .saveChanges()
                .checkToastMessage(ToastMessage.UPDATED_PAINTING.getMessage());
    }

    @ParameterizedTest
    @User
    @Artist
    @Museum
    @Painting
    @ApiLogin
    @MethodSource("provideInvalidData")
    @DisplayName("Невозможность обновить картину с невалидными данными")
    void shouldUpdatePaintingFailWithInvalidData(String paintingTitle, String paintingDescription, String errorText,
                                                 @NonNull ArtistJson artist, @NonNull MuseumJson museum, @NonNull PaintingJson painting) {
        new PaintingsPage().open()
                .checkThatPageLoaded()
                .searchPaintingByTitle(painting.title())
                .selectPainting(painting.title())
                .toEditPainting()
                .setTitle(paintingTitle)
                .selectArtist(artist.name())
                .setDescription(paintingDescription)
                .selectMuseum(museum.title())
                .clickSubmit()
                .checkErrorMessage(errorText)
        ;
    }

}
