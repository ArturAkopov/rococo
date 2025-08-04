package anbrain.qa.rococo.page.components;

import anbrain.qa.rococo.jupiter.extension.ScreenShotTestExtension;
import anbrain.qa.rococo.page.ArtistsPage;
import anbrain.qa.rococo.utils.ScreenDiffResult;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EditArtistModal extends BaseComponent<EditArtistModal> {

    private final SelenideElement
            inputArtistName,
            inputArtistPhoto,
            textAreaBiography,
            artistCard,
            submitButton;

    public EditArtistModal() {
        super($("[data-testid='modal-component']"));
        this.inputArtistName = self.$(".input[name='name']");
        this.inputArtistPhoto = self.$(".input[name='photo']");
        this.textAreaBiography = self.$(".textarea[name='biography']");
        this.artistCard = self.$(".card");
        this.submitButton = self.$("button[type='submit']");
    }

    @Nonnull
    @Step("Ввод имени художника '{artistName}'")
    public EditArtistModal setArtistName(String artistName) {
        inputArtistName.clear();
        inputArtistName.setValue(artistName);
        return this;
    }

    @Nonnull
    @Step("Загрузка фотографии художника")
    public EditArtistModal uploadPhoto(File uploadFile) {
        inputArtistPhoto.uploadFile(uploadFile);
        return this;
    }

    @Nonnull
    @Step("Ввод биографии '{biography}'")
    public EditArtistModal setBiography(String biography) {
        textAreaBiography.clear();
        textAreaBiography.setValue(biography);
        return this;
    }

    @Nonnull
    @Step("Сохранить данные художника")
    public ArtistsPage saveArtist() {
        submitButton.scrollTo().click();
        return new ArtistsPage();
    }

    @Nonnull
    @Step("Нажатие кнопки подтверждения")
    public EditArtistModal clickSubmitButton() {
        submitButton.scrollTo().click();
        return this;
    }

    @Nonnull
    @Step("Скриншот карточки редактирования Художника")
    public BufferedImage screenShotEditArtistCard() throws IOException {
        return ImageIO.read(requireNonNull(artistCard.screenshot()));
    }

    @Step("Проверка совпадения актуального изображения карточки артиста с искомым")
    @Nonnull
    public EditArtistModal checkArtistCardImage(BufferedImage expectedImage) throws IOException {
        Selenide.sleep(1000);
        assertFalse(
                new ScreenDiffResult(
                        screenShotEditArtistCard(),
                        expectedImage
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
        return this;
    }

}
