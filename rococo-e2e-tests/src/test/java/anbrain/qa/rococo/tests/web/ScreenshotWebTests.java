package anbrain.qa.rococo.tests.web;

import anbrain.qa.rococo.jupiter.annotation.ApiLogin;
import anbrain.qa.rococo.jupiter.annotation.ScreenShotTest;
import anbrain.qa.rococo.jupiter.annotation.User;
import anbrain.qa.rococo.jupiter.annotation.meta.WebTest;
import anbrain.qa.rococo.page.ArtistsPage;
import org.junit.jupiter.api.DisplayName;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@WebTest
@DisplayName("[WEB] ScreenshotTests")
public class ScreenshotWebTests {

    @ScreenShotTest(expected = "expected-artistName.png", rewriteExpected = true)
    @User
    @ApiLogin
    @DisplayName("Должно появится сообщение об ошибке при добавлении художника с пустым именем")
    void shouldAppearErrorMessageWhenAddArtistWithEmptyName(BufferedImage expected) throws IOException {
        new ArtistsPage().open()
                .checkThatPageLoaded()
                .clickAddArtistButton()
                .getEditArtistModal()
                .clickSubmitButton()
                .checkArtistCardImage(expected);
    }

    @ScreenShotTest(expected = "expected-artistImage.png", rewriteExpected = true)
    @User
    @ApiLogin
    @DisplayName("Должно появится сообщение об ошибке при добавлении художника с пустым изображением")
    void shouldAppearErrorMessageWhenAddArtistWithEmptyImage(BufferedImage expected) throws IOException {
        new ArtistsPage().open()
                .checkThatPageLoaded()
                .clickAddArtistButton()
                .getEditArtistModal()
                .setArtistName("Артурчик Красавчик")
                .clickSubmitButton()
                .checkArtistCardImage(expected);
    }

    @ScreenShotTest(expected = "expected-artistBiography.png", rewriteExpected = true)
    @User
    @ApiLogin
    @DisplayName("Должно появится сообщение об ошибке при добавлении художника с пустой биографией")
    void shouldAppearErrorMessageWhenAddArtistWithEmptyBiography(BufferedImage expected) throws IOException {
        new ArtistsPage().open()
                .checkThatPageLoaded()
                .clickAddArtistButton()
                .getEditArtistModal()
                .setArtistName("Артурчик Красавчик")
                .uploadPhoto(new File("rococo-e2e-tests/src/test/resources/screenshots/avatar.jpg").getAbsoluteFile())
                .clickSubmitButton()
                .checkArtistCardImage(expected);
    }

}
