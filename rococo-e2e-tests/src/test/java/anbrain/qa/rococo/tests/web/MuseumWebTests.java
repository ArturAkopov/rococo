package anbrain.qa.rococo.tests.web;

import anbrain.qa.rococo.jupiter.annotation.*;
import anbrain.qa.rococo.jupiter.annotation.meta.WebTest;
import anbrain.qa.rococo.model.rest.MuseumJson;
import anbrain.qa.rococo.page.MuseumsPage;
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
@DisplayName("[WEB] Проверка взаимодействия с музеями")
public class MuseumWebTests {

    @NonNull
    private static Stream<Arguments> provideData() {
        return Stream.of(
                Arguments.of(
                        RandomDataUtils.randomMuseumTitle().substring(0, 3),
                        RandomDataUtils.randomCity().substring(0,3),
                        RandomDataUtils.randomString().substring(0, 11)
                ),
                Arguments.of(
                        ((RandomDataUtils.randomMuseumTitle() + "a".repeat(255)).substring(0, 255)),
                        ((RandomDataUtils.randomCity() + "a".repeat(255)).substring(0, 255)),
                        ((RandomDataUtils.randomString() + "a".repeat(2000)).substring(0, 2000))
                        )
        );
    }

    @ParameterizedTest
    @User
    @ApiLogin
    @MethodSource("provideData")
    @DisplayName("Должен успешно добавиться музей")
    void shouldAddedMuseumSuccess(String title, String cityName, String description) {
        new MuseumsPage().open()
                .checkThatPageLoaded()
                .clickAddMuseumButton()
                .getEditMuseumModal()
                .setMuseumTitle(title)
                .selectCounty(RandomDataUtils.randomCountry())
                .setCityName(cityName)
                .uploadPhoto(RandomDataUtils.avatarFile())
                .setDescription(description)
                .saveMuseum()
                .checkToastMessage(ToastMessage.ADDED_MUSEUM.getMessage()+title);
    }

    @Test
    @Museum
    @DisplayName("Должен успешно найтись музей")
    void shouldSearchedMuseumSuccess(@NonNull MuseumJson museumJson) {
        new MuseumsPage().open()
                .checkThatPageLoaded()
                .searchMuseumByTitle(museumJson.title())
                .checkThatMuseumExist(museumJson.title());
    }

    @NonNull
    private static Stream<Arguments> provideInvalidData() {
        return Stream.of(
                Arguments.of(
                        RandomDataUtils.randomMuseumTitle().substring(0, 2),
                        RandomDataUtils.randomString(),
                        RandomDataUtils.randomCity(),
                        TextError.THE_TITLE_CANNOT_BE_SHORTER_THAN_3_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        (RandomDataUtils.randomMuseumTitle() + "a".repeat(256).substring(0, 256)),
                        RandomDataUtils.randomString(),
                        RandomDataUtils.randomCity(),
                        TextError.THE_TITLE_CANNOT_BE_LONGER_THAN_255_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        RandomDataUtils.randomMuseumTitle(),
                        RandomDataUtils.randomString().substring(0, 10),
                        RandomDataUtils.randomCity(),
                        TextError.THE_DESCRIPTION_CANNOT_BE_SHORTER_THAN_10_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        RandomDataUtils.randomMuseumTitle(),
                        (RandomDataUtils.randomString() + "a".repeat(2000)).substring(0, 2001),
                        RandomDataUtils.randomCity(),
                        TextError.THE_DESCRIPTION_CANNOT_BE_LONGER_THAN_2000_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        RandomDataUtils.randomMuseumTitle(),
                        RandomDataUtils.randomString(),
                        RandomDataUtils.randomCity().substring(0,2),
                        TextError.THE_CITY_NAME_CANNOT_BE_SHORTER_THAN_3_CHARACTERS.getMessage()
                ),
                Arguments.of(
                        RandomDataUtils.randomMuseumTitle(),
                        RandomDataUtils.randomString(),
                        (RandomDataUtils.randomCity()+"a".repeat(256).substring(0, 256)),
                        TextError.THE_CITY_NAME_CANNOT_BE_LONGER_THAN_255_CHARACTERS.getMessage()
                ));
    }

    @ParameterizedTest
    @User
    @ApiLogin
    @MethodSource("provideInvalidData")
    @DisplayName("Невозможность добавить музей с невалидными данными")
    void shouldAddMuseumFailWithInvalidData(String title, String description, String cityName, String errorText) {
        new MuseumsPage().open()
                .checkThatPageLoaded()
                .clickAddMuseumButton()
                .getEditMuseumModal()
                .setMuseumTitle(title)
                .selectCounty(RandomDataUtils.randomCountry())
                .setCityName(cityName)
                .uploadPhoto(RandomDataUtils.avatarFile())
                .setDescription(description)
                .clickSubmitButton()
                .checkErrorMessage(errorText)
        ;
    }

    @ParameterizedTest
    @User
    @Museum
    @ApiLogin
    @MethodSource("provideData")
    @DisplayName("Должен успешно обновиться музей")
    void shouldUpdatedMuseumSuccess(String title, String cityName, String description,
                                      @NonNull MuseumJson museum) {
        new MuseumsPage().open()
                .checkThatPageLoaded()
                .selectMuseum(museum.title())
                .toEditMuseum()
                .uploadPhoto(RandomDataUtils.avatarFile())
                .setMuseumTitle(title)
                .selectCounty(RandomDataUtils.randomCountry())
                .setCityName(cityName)
                .setDescription(description)
                .saveMuseum()
                .checkToastMessage(ToastMessage.UPDATED_MUSEUM.getMessage()+title);
    }

    @ParameterizedTest
    @User
    @Museum
    @ApiLogin
    @MethodSource("provideInvalidData")
    @DisplayName("Невозможность обновить музей с невалидными данными")
    void shouldUpdateMuseumFailWithInvalidData(String title, String description, String cityName, String errorText,
                                                 @NonNull MuseumJson museum) {
        new MuseumsPage().open()
                .checkThatPageLoaded()
                .selectMuseum(museum.title())
                .toEditMuseum()
                .uploadPhoto(RandomDataUtils.avatarFile())
                .setMuseumTitle(title)
                .selectCounty(RandomDataUtils.randomCountry())
                .setCityName(cityName)
                .setDescription(description)
                .clickSubmitButton()
                .checkErrorMessage(errorText);
    }

}
